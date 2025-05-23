package ma.emsi.charitywebapp.services;

import ma.emsi.charitywebapp.entities.ActionCharite;
import ma.emsi.charitywebapp.entities.Categorie;
import ma.emsi.charitywebapp.entities.Organisation;
import ma.emsi.charitywebapp.repositories.ActionChariteRepository;
import ma.emsi.charitywebapp.repositories.DonRepository;
import ma.emsi.charitywebapp.repositories.OrganisationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.LinkedHashMap;

@Service
public class ActionChariteServiceImp implements ActionChariteService {

    private static final Logger logger = LoggerFactory.getLogger(ActionChariteServiceImp.class);

    public final ActionChariteRepository actionChariteRepository;
    private final DonRepository donRepository;
    private final OrganisationRepository organisationRepository;

    @Autowired
    public ActionChariteServiceImp(ActionChariteRepository actionChariteRepository, DonRepository donRepository, OrganisationRepository organisationRepository) {
        this.actionChariteRepository = actionChariteRepository;
        this.donRepository = donRepository;
        this.organisationRepository = organisationRepository;
    }

    @Override
    public ActionCharite saveActionCharite(ActionCharite actionCharite) {
        return actionChariteRepository.save(actionCharite);
    }

    @Override
    public ActionCharite updateActionCharite(ActionCharite actionCharite) {
        return actionChariteRepository.save(actionCharite);
    }

    @Override
    public Optional<ActionCharite> getActionChariteById(Long id) {
        return actionChariteRepository.findById(id);
    }

    @Override
    public List<ActionCharite> getAllActionsCharite() {
        return actionChariteRepository.findAll();
    }

    @Override
    public List<ActionCharite> getActionsByOrganisation(Organisation organisation) {
        logger.debug("Recherche des actions pour l'organisation: id={}, nom={}", 
            organisation != null ? organisation.getId() : "null", 
            organisation != null ? organisation.getNom() : "null");
        
        if (organisation == null) {
            logger.error("Organisation est null dans getActionsByOrganisation");
            return List.of(); // Retourne une liste vide si l'organisation est null
        }
        
        List<ActionCharite> actions = actionChariteRepository.findByOrganisation(organisation);
        logger.debug("Nombre d'actions trouvées pour l'organisation {}: {}", organisation.getId(), actions.size());
        
        if (actions.isEmpty()) {
            logger.warn("Aucune action trouvée pour l'organisation id={}", organisation.getId());
        } else {
            logger.debug("Actions trouvées: {}", actions.stream().map(a -> a.getId() + ":" + a.getTitre()).toList());
        }
        
        return actions;
    }

    @Override
    public List<ActionCharite> getActionsByCategorie(Categorie categorie) {
        return actionChariteRepository.findByCategorie(categorie);
    }

    @Override
    public List<ActionCharite> getActionsByStatut(String statut) {
        return actionChariteRepository.findByStatut(statut);
    }

    @Override
    public List<ActionCharite> searchActionsByTitle(String keyword) {
        return actionChariteRepository.findByTitreContainingIgnoreCase(keyword);
    }

    @Override
    public List<ActionCharite> getTopActions() {
        return actionChariteRepository.findTop10ByOrderByMontantCollecteDesc();
    }

    @Override
    public Optional<ActionCharite> getActionById(Long id) {
        return actionChariteRepository.findById(id);
    }

    @Override
    public void deleteActionCharite(Long id) {
        actionChariteRepository.deleteById(id);
    }

    @Override
    public void updateMontantCollecte(Long actionId) {
        ActionCharite action = actionChariteRepository.findById(actionId)
                .orElseThrow(() -> new RuntimeException("Action de charité non trouvée"));

        Double totalDons = donRepository.sumMontantByAction(action);
        if (totalDons != null) {
            action.setMontantCollecte(totalDons);
            actionChariteRepository.save(action);
        }
    }

    @Override
    public String saveImage(MultipartFile imageFile) {
        if (imageFile == null || imageFile.isEmpty())
            return null;
        try {
            String uploadsDir = "uploads/";
            Path uploadsPath = Paths.get("src/main/resources/static/" + uploadsDir);
            if (!Files.exists(uploadsPath)) {
                Files.createDirectories(uploadsPath);
            }
            String filename = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
            Path filePath = uploadsPath.resolve(filename);
            Files.copy(imageFile.getInputStream(), filePath);
            return "/" + uploadsDir + filename;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void archiverAction(Long id) {
        ActionCharite action = actionChariteRepository.findById(id).orElseThrow();
        action.setStatut("archive");
        actionChariteRepository.save(action);
    }

    @Override
    public long countAll() {
        return actionChariteRepository.count();
    }

    @Override
    public long countActive() {
        return actionChariteRepository.countByStatut("ACTIVE");
    }

    @Override
    public Map<String, Integer> getActionsParMois() {
        List<ActionCharite> actions = actionChariteRepository.findAll();
        Map<String, Integer> result = new LinkedHashMap<>();
        for (ActionCharite action : actions) {
            if (action.getDateCreation() != null) {
                String mois = action.getDateCreation().getYear() + "-"
                        + String.format("%02d", action.getDateCreation().getMonthValue());
                result.put(mois, result.getOrDefault(mois, 0) + 1);
            }
        }
        return result;
    }

    @Override
    public List<ActionCharite> findActionsByOrganisationAndStatut(String email, String statut) {
        // Récupérer l'organisation par email
        Organisation organisation = organisationRepository.findByAdminEmail(email)
                .orElseThrow(() -> new RuntimeException("Organisation introuvable"));
        
        // Filtrer par statut si demandé
        if (statut != null && !statut.isEmpty()) {
            return actionChariteRepository.findByOrganisationAndStatut(organisation, statut);
        } else {
            // Sinon retourner toutes les actions de l'organisation
            return actionChariteRepository.findByOrganisation(organisation);
        }
    }

    @Override
    public ActionCharite findById(Long id) {
        return actionChariteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Action introuvable avec l'ID: " + id));
    }

    @Override
    public List<ActionCharite> getActionsByOrganisationAndStatut(Organisation organisation, String statut) {
        logger.debug("Recherche des actions pour l'organisation id={} avec statut='{}'", 
            organisation != null ? organisation.getId() : "null", statut);
        
        if (organisation == null) {
            logger.error("Organisation est null dans getActionsByOrganisationAndStatut");
            return List.of();
        }
        
        List<ActionCharite> actions;
        if (statut != null && !statut.isEmpty()) {
            actions = actionChariteRepository.findByOrganisationAndStatut(organisation, statut);
            logger.debug("Recherche par organisation et statut: {}, trouvées: {}", statut, actions.size());
        } else {
            actions = actionChariteRepository.findByOrganisation(organisation);
            logger.debug("Recherche par organisation uniquement, trouvées: {}", actions.size());
        }
        
        if (actions.isEmpty()) {
            logger.warn("Aucune action trouvée pour l'organisation id={} et statut='{}'", 
                organisation.getId(), statut != null ? statut : "tous");
        }
        
        return actions;
    }

    @Override
    public List<ActionCharite> findByOrganisationIdDirect(Long organisationId) {
        logger.debug("Recherche directe des actions par ID d'organisation: {}", organisationId);
        List<ActionCharite> actions = actionChariteRepository.findByOrganisationIdDirect(organisationId);
        logger.debug("Trouvé {} actions pour l'organisation ID {}", actions.size(), organisationId);
        return actions;
    }
    
    @Override
    public List<ActionCharite> findAllWithOrganisation() {
        logger.debug("Recherche de toutes les actions avec organisation");
        List<ActionCharite> actions = actionChariteRepository.findAllWithOrganisation();
        logger.debug("Trouvé {} actions avec organisation", actions.size());
        return actions;
    }
}
