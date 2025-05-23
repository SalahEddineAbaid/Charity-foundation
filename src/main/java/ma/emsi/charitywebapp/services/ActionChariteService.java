package ma.emsi.charitywebapp.services;

import ma.emsi.charitywebapp.entities.ActionCharite;
import ma.emsi.charitywebapp.entities.Categorie;
import ma.emsi.charitywebapp.entities.Organisation;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ActionChariteService {
    ActionCharite saveActionCharite(ActionCharite actionCharite);

    ActionCharite updateActionCharite(ActionCharite actionCharite);

    Optional<ActionCharite> getActionChariteById(Long id);

    List<ActionCharite> getAllActionsCharite();

    List<ActionCharite> getActionsByOrganisation(Organisation organisation);

    List<ActionCharite> getActionsByCategorie(Categorie categorie);

    List<ActionCharite> getActionsByStatut(String statut);

    List<ActionCharite> searchActionsByTitle(String keyword);

    List<ActionCharite> getTopActions();

    Optional<ActionCharite> getActionById(Long id);

    void deleteActionCharite(Long id);

    void updateMontantCollecte(Long actionId);

    String saveImage(MultipartFile imageFile);

    void archiverAction(Long id);

    long countAll();

    long countActive();

    Map<String, Integer> getActionsParMois();

    List<ActionCharite> findActionsByOrganisationAndStatut(String email, String statut);
    ActionCharite findById(Long id);
    
    List<ActionCharite> getActionsByOrganisationAndStatut(Organisation organisation, String statut);
    
    // Nouvelles méthodes pour accéder directement par ID d'organisation
    List<ActionCharite> findByOrganisationIdDirect(Long organisationId);
    List<ActionCharite> findAllWithOrganisation();
}