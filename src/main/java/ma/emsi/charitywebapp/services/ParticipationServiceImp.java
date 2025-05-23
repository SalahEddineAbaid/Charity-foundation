package ma.emsi.charitywebapp.services;

import ma.emsi.charitywebapp.entities.ActionCharite;
import ma.emsi.charitywebapp.entities.Participation;
import ma.emsi.charitywebapp.entities.Utilisateur;
import ma.emsi.charitywebapp.entities.Notification;
import ma.emsi.charitywebapp.repositories.ParticipationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class ParticipationServiceImp implements ParticipationService {

    private static final Logger logger = LoggerFactory.getLogger(ParticipationServiceImp.class);

    private final ParticipationRepository participationRepository;
    private final NotificationService notificationService;

    @Autowired
    public ParticipationServiceImp(ParticipationRepository participationRepository, NotificationService notificationService) {
        this.participationRepository = participationRepository;
        this.notificationService = notificationService;
    }

    @Override
    public Participation saveParticipation(Participation participation) {
        logger.debug("Sauvegarde d'une participation pour l'utilisateur ID {} et l'action ID {}",
                participation.getUtilisateur() != null ? participation.getUtilisateur().getId() : "null",
                participation.getAction() != null ? participation.getAction().getId() : "null");
        
        Participation savedParticipation = participationRepository.save(participation);
        
        // Créer une notification pour l'utilisateur
        createParticipationNotification(savedParticipation);
        
        return savedParticipation;
    }

    /**
     * Crée une notification pour informer l'utilisateur de sa participation
     */
    private void createParticipationNotification(Participation participation) {
        Notification notification = new Notification();
        notification.setUtilisateur(participation.getUtilisateur());
        notification.setTitre("Participation enregistrée");
        
        String formattedDate = "";
        if (participation.getAction().getDateDebut() != null) {
            formattedDate = participation.getAction().getDateDebut()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm"));
        }
        
        notification.setContenu("Votre participation à l'action \"" + 
                participation.getAction().getTitre() + "\"" + 
                (formattedDate.isEmpty() ? "" : " prévue le " + formattedDate) + 
                " a bien été enregistrée avec le statut: " + participation.getStatut());
        
        notification.setDateCreation(LocalDateTime.now());
        notification.setLu(false);
        notification.setAction(participation.getAction());
        
        notificationService.saveNotification(notification);
    }

    @Override
    public Participation updateParticipation(Participation participation) {
        // Récupérer l'ancienne participation pour comparer les changements
        Optional<Participation> oldParticipationOpt = participationRepository.findById(participation.getId());
        
        Participation updated = participationRepository.save(participation);
        
        // Si la participation existait déjà et que son statut a changé, créer une notification
        if (oldParticipationOpt.isPresent()) {
            Participation oldParticipation = oldParticipationOpt.get();
            if (!oldParticipation.getStatut().equals(updated.getStatut())) {
                createStatusChangeNotification(updated, oldParticipation.getStatut(), updated.getStatut());
            }
        }
        
        return updated;
    }

    @Override
    public Optional<Participation> getParticipationById(Long id) {
        logger.debug("Récupération de la participation avec ID {}", id);
        return participationRepository.findById(id);
    }

    @Override
    public List<Participation> getAllParticipations() {
        return participationRepository.findAll();
    }

    @Override
    public List<Participation> getParticipationsByUtilisateur(Utilisateur utilisateur) {
        logger.debug("Récupération des participations pour l'utilisateur ID {}", utilisateur.getId());
        return participationRepository.findByUtilisateur(utilisateur);
    }

    @Override
    public List<Participation> getParticipationsByAction(ActionCharite action) {
        logger.debug("Récupération des participations pour l'action ID {}", action.getId());
        return participationRepository.findByAction(action);
    }

    @Override
    public Optional<Participation> getParticipationByUtilisateurAndAction(Utilisateur utilisateur, ActionCharite action) {
        return participationRepository.findByUtilisateurAndAction(utilisateur, action);
    }

    @Override
    public void deleteParticipation(Long id) {
        logger.debug("Suppression de la participation avec ID {}", id);
        participationRepository.deleteById(id);
    }

    @Override
    public long countByAction(ActionCharite action) {
        logger.debug("Comptage des participations pour l'action ID {}", action.getId());
        return participationRepository.countByAction(action);
    }

    @Override
    public void updateParticipationStatut(Long participationId, String statut) {
        logger.debug("Mise à jour du statut pour la participation ID {} vers {}", participationId, statut);
        Optional<Participation> optParticipation = participationRepository.findById(participationId);
        if (optParticipation.isPresent()) {
            Participation participation = optParticipation.get();
            String oldStatut = participation.getStatut();
            participation.setStatut(statut);
            participationRepository.save(participation);
            
            // Créer une notification pour informer l'utilisateur du changement de statut
            if (!statut.equals(oldStatut)) {
                createStatusChangeNotification(participation, oldStatut, statut);
            }
        } else {
            logger.warn("Participation avec ID {} non trouvée pour la mise à jour du statut", participationId);
        }
    }
    
    /**
     * Crée une notification pour informer l'utilisateur du changement de statut de sa participation
     */
    private void createStatusChangeNotification(Participation participation, String oldStatut, String newStatut) {
        Notification notification = new Notification();
        notification.setUtilisateur(participation.getUtilisateur());
        notification.setTitre("Mise à jour de votre participation");
        notification.setContenu("Le statut de votre participation à l'action \"" + 
                participation.getAction().getTitre() + "\" a été modifié de \"" + 
                oldStatut + "\" à \"" + newStatut + "\".");
        notification.setDateCreation(LocalDateTime.now());
        notification.setLu(false);
        notification.setAction(participation.getAction());
        
        notificationService.saveNotification(notification);
    }

    @Override
    public int countParticipationsByOrganisationId(Long organisationId) {
        // Utilisez une requête JPQL pour compter directement
        return participationRepository.countByActionOrganisationId(organisationId);
    }
}