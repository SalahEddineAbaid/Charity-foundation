package ma.emsi.charitywebapp.services;

import ma.emsi.charitywebapp.entities.ActionCharite;
import ma.emsi.charitywebapp.entities.Don;
import ma.emsi.charitywebapp.entities.Notification;
import ma.emsi.charitywebapp.entities.Utilisateur;
import ma.emsi.charitywebapp.repositories.DonRepository;
import ma.emsi.charitywebapp.services.ActionChariteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.LinkedHashMap;

@Service
public class DonServiceImp implements DonService {

    private final DonRepository donRepository;
    private final ActionChariteService actionChariteService;
    private final NotificationService notificationService;

    @Autowired
    public DonServiceImp(DonRepository donRepository, ActionChariteService actionChariteService, NotificationService notificationService) {
        this.donRepository = donRepository;
        this.actionChariteService = actionChariteService;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional
    public Don saveDon(Don don) {
        Don savedDon = donRepository.save(don);
        actionChariteService.updateMontantCollecte(don.getAction().getId());
        
        // Créer une notification pour le donateur
        createDonNotification(don);
        
        return savedDon;
    }

    /**
     * Crée une notification pour informer l'utilisateur de son don
     */
    private void createDonNotification(Don don) {
        Notification notification = new Notification();
        notification.setUtilisateur(don.getDonateur());
        notification.setTitre("Merci pour votre don!");
        notification.setContenu("Votre don de " + don.getMontant() + " € pour l'action \"" + 
                don.getAction().getTitre() + "\" a bien été enregistré.");
        notification.setDateCreation(LocalDateTime.now());
        notification.setLu(false);
        notification.setAction(don.getAction());
        
        notificationService.saveNotification(notification);
    }

    @Override
    @Transactional
    public Don updateDon(Don don) {
        // Récupérer l'ancien don pour comparer les changements
        Optional<Don> oldDonOpt = donRepository.findById(don.getId());
        
        Don updatedDon = donRepository.save(don);
        actionChariteService.updateMontantCollecte(don.getAction().getId());
        
        // Si le don existait déjà et que son statut a changé, créer une notification
        if (oldDonOpt.isPresent()) {
            Don oldDon = oldDonOpt.get();
            if ((oldDon.getStatutPaiement() == null && don.getStatutPaiement() != null) || 
                (oldDon.getStatutPaiement() != null && !oldDon.getStatutPaiement().equals(don.getStatutPaiement()))) {
                
                createDonStatusNotification(updatedDon);
            }
        }
        
        return updatedDon;
    }

    /**
     * Crée une notification pour informer l'utilisateur du changement de statut de son don
     */
    private void createDonStatusNotification(Don don) {
        Notification notification = new Notification();
        notification.setUtilisateur(don.getDonateur());
        
        if ("confirmé".equalsIgnoreCase(don.getStatutPaiement()) || "payé".equalsIgnoreCase(don.getStatutPaiement())) {
            notification.setTitre("Don confirmé");
            notification.setContenu("Votre don de " + don.getMontant() + " € pour l'action \"" + 
                    don.getAction().getTitre() + "\" a bien été confirmé. Merci pour votre générosité!");
        } else {
            notification.setTitre("Mise à jour de votre don");
            notification.setContenu("Le statut de votre don de " + don.getMontant() + " € pour l'action \"" + 
                    don.getAction().getTitre() + "\" a été mis à jour: " + don.getStatutPaiement());
        }
        
        notification.setDateCreation(LocalDateTime.now());
        notification.setLu(false);
        notification.setAction(don.getAction());
        
        notificationService.saveNotification(notification);
    }

    @Override
    public Optional<Don> getDonById(Long id) {
        return donRepository.findById(id);
    }

    @Override
    public List<Don> getAllDons() {
        return donRepository.findAll();
    }

    @Override
    public List<Don> getDonsByDonateur(Utilisateur donateur) {
        return donRepository.findByDonateur(donateur);
    }

    @Override
    public List<Don> getDonsByAction(ActionCharite action) {
        return donRepository.findByAction(action);
    }

    @Override
    public Double getTotalDonsByAction(ActionCharite action) {
        Double total = donRepository.sumMontantByAction(action);
        return total != null ? total : 0.0;
    }

    @Override
    public Double getTotalDonsByDonateur(Utilisateur donateur) {
        Double total = donRepository.sumMontantByDonateur(donateur);
        return total != null ? total : 0.0;
    }

    @Override
    public void deleteDon(Long id) {
        Don don = donRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Don non trouvé"));
        Long actionId = don.getAction().getId();
        donRepository.deleteById(id);
        actionChariteService.updateMontantCollecte(actionId);
    }

    @Override
    public String genererRecu(Long donId) {
        Don don = donRepository.findById(donId)
                .orElseThrow(() -> new RuntimeException("Don non trouvé"));
        return don.genererRecu();
    }

    @Override
    public Don getDonByStripeSessionId(String sessionId) {
        return donRepository.findByStripeSessionId(sessionId).orElse(null);
    }

    @Override
    public long countAll() {
        return donRepository.count();
    }

    @Override
    public double sumMontant() {
        Double sum = donRepository.sumMontant();
        return sum != null ? sum : 0.0;
    }

    @Override
    public Map<String, Double> getDonsParMois() {
        return donRepository.sumMontantGroupByMonth();
    }

    @Override
    public BigDecimal getTotalDonationAmount() {
        return donRepository.sumTotalAmount();
    }

    @Override
    public Map<String, BigDecimal> getDonationsByMonth() {
        Map<String, Double> raw = donRepository.sumMontantGroupByMonth();
        Map<String, BigDecimal> result = new LinkedHashMap<>();
        for (Map.Entry<String, Double> entry : raw.entrySet()) {
            result.put(entry.getKey(), BigDecimal.valueOf(entry.getValue()));
        }
        return result;
    }

    @Override
    public List<Don> getDonsByUtilisateur(Utilisateur utilisateur) {
        return donRepository.findByDonateur(utilisateur);
    }

}