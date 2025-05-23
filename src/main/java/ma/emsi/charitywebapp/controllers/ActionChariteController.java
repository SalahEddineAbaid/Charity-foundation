package ma.emsi.charitywebapp.controllers;

import ma.emsi.charitywebapp.DTOs.ActionChariteDTO;
import ma.emsi.charitywebapp.entities.ActionCharite;
import ma.emsi.charitywebapp.entities.Categorie;
import ma.emsi.charitywebapp.entities.Organisation;
import ma.emsi.charitywebapp.services.ActionChariteService;
import ma.emsi.charitywebapp.services.CategorieService;
import ma.emsi.charitywebapp.services.OrganisationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/actions")
public class ActionChariteController {

    private final ActionChariteService actionChariteService;
    private final OrganisationService organisationService;
    private final CategorieService categorieService;

    @Autowired
    public ActionChariteController(ActionChariteService actionChariteService, OrganisationService organisationService,
            CategorieService categorieService) {
        this.actionChariteService = actionChariteService;
        this.organisationService = organisationService;
        this.categorieService = categorieService;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated() and @organisationService.getOrganisationById(#actionChariteDTO.organisationId).get().getAdmin().getId() == authentication.principal.id")
    public ResponseEntity<?> createAction(@RequestBody ActionChariteDTO actionChariteDTO) {
        Optional<Organisation> organisation = organisationService
                .getOrganisationById(actionChariteDTO.getOrganisationId());
        if (organisation.isEmpty()) {
            return ResponseEntity.badRequest().body("Organisation non trouvée");
        }

        Optional<Categorie> categorie = categorieService.getCategorieById(actionChariteDTO.getCategorieId());
        if (categorie.isEmpty()) {
            return ResponseEntity.badRequest().body("Catégorie non trouvée");
        }

        ActionCharite actionCharite = new ActionCharite();
        actionCharite.setTitre(actionChariteDTO.getTitre());
        actionCharite.setDescription(actionChariteDTO.getDescription());
        actionCharite.setDateDebut(actionChariteDTO.getDateDebut());
        actionCharite.setDateFin(actionChariteDTO.getDateFin());
        actionCharite.setLieu(actionChariteDTO.getLieu());
        actionCharite.setObjectifCollecte(actionChariteDTO.getObjectifCollecte());
        actionCharite.setStatut(actionChariteDTO.getStatut());
        actionCharite.setOrganisation(organisation.get());
        actionCharite.setCategorie(categorie.get());

        ActionCharite savedAction = actionChariteService.saveActionCharite(actionCharite);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(savedAction));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getActionById(@PathVariable Long id) {
        Optional<ActionCharite> action = actionChariteService.getActionChariteById(id);
        return action.map(value -> ResponseEntity.ok(convertToDTO(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<ActionChariteDTO>> getAllActions() {
        List<ActionCharite> actions = actionChariteService.getAllActionsCharite();
        List<ActionChariteDTO> actionDTOs = actions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(actionDTOs);
    }

    @GetMapping("/organisation/{organisationId}")
    public ResponseEntity<List<ActionChariteDTO>> getActionsByOrganisation(@PathVariable Long organisationId) {
        Optional<Organisation> organisation = organisationService.getOrganisationById(organisationId);
        if (organisation.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<ActionCharite> actions = actionChariteService.getActionsByOrganisation(organisation.get());
        List<ActionChariteDTO> actionDTOs = actions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(actionDTOs);
    }

    @GetMapping("/categorie/{categorieId}")
    public ResponseEntity<List<ActionChariteDTO>> getActionsByCategorie(@PathVariable Long categorieId) {
        Optional<Categorie> categorie = categorieService.getCategorieById(categorieId);
        if (categorie.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<ActionCharite> actions = actionChariteService.getActionsByCategorie(categorie.get());
        List<ActionChariteDTO> actionDTOs = actions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(actionDTOs);
    }

    @GetMapping("/statut/{statut}")
    public ResponseEntity<List<ActionChariteDTO>> getActionsByStatut(@PathVariable String statut) {
        List<ActionCharite> actions = actionChariteService.getActionsByStatut(statut);
        List<ActionChariteDTO> actionDTOs = actions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(actionDTOs);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ActionChariteDTO>> searchActions(@RequestParam String keyword) {
        List<ActionCharite> actions = actionChariteService.searchActionsByTitle(keyword);
        List<ActionChariteDTO> actionDTOs = actions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(actionDTOs);
    }

    @GetMapping("/top")
    public ResponseEntity<List<ActionChariteDTO>> getTopActions() {
        List<ActionCharite> actions = actionChariteService.getTopActions();
        List<ActionChariteDTO> actionDTOs = actions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(actionDTOs);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @actionChariteService.getActionChariteById(#id).get().getOrganisation().getAdmin().getId() == authentication.principal.id")
    public ResponseEntity<?> updateAction(@PathVariable Long id, @RequestBody ActionChariteDTO actionChariteDTO) {
        Optional<ActionCharite> optionalAction = actionChariteService.getActionChariteById(id);
        if (optionalAction.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ActionCharite actionCharite = optionalAction.get();
        actionCharite.setTitre(actionChariteDTO.getTitre());
        actionCharite.setDescription(actionChariteDTO.getDescription());
        actionCharite.setDateDebut(actionChariteDTO.getDateDebut());
        actionCharite.setDateFin(actionChariteDTO.getDateFin());
        actionCharite.setLieu(actionChariteDTO.getLieu());
        actionCharite.setObjectifCollecte(actionChariteDTO.getObjectifCollecte());
        actionCharite.setStatut(actionChariteDTO.getStatut());

        if (actionChariteDTO.getCategorieId() != null) {
            Optional<Categorie> categorie = categorieService.getCategorieById(actionChariteDTO.getCategorieId());
            categorie.ifPresent(actionCharite::setCategorie);
        }

        ActionCharite updatedAction = actionChariteService.updateActionCharite(actionCharite);
        return ResponseEntity.ok(convertToDTO(updatedAction));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @actionChariteService.getActionChariteById(#id).get().getOrganisation().getAdmin().getId() == authentication.principal.id")
    public ResponseEntity<?> deleteAction(@PathVariable Long id) {
        Optional<ActionCharite> action = actionChariteService.getActionChariteById(id);
        if (action.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        actionChariteService.deleteActionCharite(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/actions")
    @PreAuthorize("hasAnyRole('UTILISATEUR','ORGANISATION')")
    public String listActions(@RequestParam(required = false) Long categorieId, org.springframework.ui.Model model) {
        List<ActionCharite> actions = (categorieId != null)
                ? actionChariteService
                        .getActionsByCategorie(categorieService.getCategorieById(categorieId).orElse(null))
                : actionChariteService.getAllActionsCharite();
        model.addAttribute("actions", actions);
        model.addAttribute("categories", categorieService.getAllCategories());
        model.addAttribute("selectedCategorieId", categorieId);
        return "user/actions";
    }

    private ActionChariteDTO convertToDTO(ActionCharite action) {
        ActionChariteDTO dto = new ActionChariteDTO();
        dto.setId(action.getId());
        dto.setTitre(action.getTitre());
        dto.setDescription(action.getDescription());
        dto.setDateDebut(action.getDateDebut());
        dto.setDateFin(action.getDateFin());
        dto.setLieu(action.getLieu());
        dto.setObjectifCollecte(action.getObjectifCollecte());
        dto.setMontantCollecte(action.getMontantCollecte());
        dto.setStatut(action.getStatut());

        if (action.getCategorie() != null) {
            dto.setCategorieId(action.getCategorie().getId());
            dto.setCategorieNom(action.getCategorie().getNom());
        }

        if (action.getOrganisation() != null) {
            dto.setOrganisationId(action.getOrganisation().getId());
            dto.setOrganisationNom(action.getOrganisation().getNom());
        }

        return dto;
    }
}
