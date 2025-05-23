package ma.emsi.charitywebapp.controllers;

import ma.emsi.charitywebapp.DTOs.OrganisationDTO;
import ma.emsi.charitywebapp.entities.Organisation;
import ma.emsi.charitywebapp.entities.Utilisateur;
import ma.emsi.charitywebapp.services.OrganisationService;
import ma.emsi.charitywebapp.services.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/organisations")
public class OrganisationController {

    private final OrganisationService organisationService;
    private final UtilisateurService userService;

    @Autowired
    public OrganisationController(OrganisationService organisationService, UtilisateurService userService) {
        this.organisationService = organisationService;
        this.userService = userService;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createOrganisation(@RequestBody OrganisationDTO organisationDTO) {
        Optional<Utilisateur> admin = userService.getUserById(organisationDTO.getAdminId());
        if (admin.isEmpty()) {
            return ResponseEntity.badRequest().body("Administrateur non trouvé");
        }

        Organisation organisation = new Organisation();
        organisation.setNom(organisationDTO.getNom());
        organisation.setDescription(organisationDTO.getDescription());
        organisation.setAdresseLegale(organisationDTO.getAdresseLegale());
        organisation.setNumeroIdentificationFiscale(organisationDTO.getNumeroIdentificationFiscale());
        organisation.setLogo(organisationDTO.getLogo());
        organisation.setContactPrincipal(organisationDTO.getContactPrincipal());
        organisation.setAdmin(admin.get());

        Organisation savedOrganisation = organisationService.saveOrganisation(organisation);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(savedOrganisation));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrganisationById(@PathVariable Long id) {
        Optional<Organisation> organisation = organisationService.getOrganisationById(id);
        return organisation.map(value -> ResponseEntity.ok(convertToDTO(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<OrganisationDTO>> getAllOrganisations() {
        List<Organisation> organisations = organisationService.getAllOrganisations();
        List<OrganisationDTO> organisationDTOs = organisations.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(organisationDTOs);
    }

    @GetMapping("/validated/{validated}")
    public ResponseEntity<List<OrganisationDTO>> getOrganisationsByValidated(@PathVariable boolean validated) {
        List<Organisation> organisations = organisationService.getOrganisationsByValidated(validated);
        List<OrganisationDTO> organisationDTOs = organisations.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(organisationDTOs);
    }

    @GetMapping("/admin/{adminId}")
    public ResponseEntity<List<OrganisationDTO>> getOrganisationsByAdmin(@PathVariable Long adminId) {
        Optional<Utilisateur> admin = userService.getUserById(adminId);
        if (admin.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<Organisation> organisations = organisationService.getOrganisationsByAdmin(admin.get());
        List<OrganisationDTO> organisationDTOs = organisations.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(organisationDTOs);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @organisationService.getOrganisationById(#id).get().getAdmin().getId() == authentication.principal.id")
    public ResponseEntity<?> updateOrganisation(@PathVariable Long id, @RequestBody OrganisationDTO organisationDTO) {
        Optional<Organisation> optionalOrganisation = organisationService.getOrganisationById(id);
        if (optionalOrganisation.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Organisation organisation = optionalOrganisation.get();
        organisation.setNom(organisationDTO.getNom());
        organisation.setDescription(organisationDTO.getDescription());
        organisation.setAdresseLegale(organisationDTO.getAdresseLegale());
        organisation.setNumeroIdentificationFiscale(organisationDTO.getNumeroIdentificationFiscale());
        organisation.setLogo(organisationDTO.getLogo());
        organisation.setContactPrincipal(organisationDTO.getContactPrincipal());

        if (organisationDTO.getAdminId() != null) {
            Optional<Utilisateur> admin = userService.getUserById(organisationDTO.getAdminId());
            admin.ifPresent(organisation::setAdmin);
        }

        Organisation updatedOrganisation = organisationService.updateOrganisation(organisation);
        return ResponseEntity.ok(convertToDTO(updatedOrganisation));
    }

    @PutMapping("/validate/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> validateOrganisation(@PathVariable Long id) {
        try {
            Organisation validatedOrganisation = organisationService.validateOrganisation(id);
            return ResponseEntity.ok(convertToDTO(validatedOrganisation));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @organisationService.getOrganisationById(#id).get().getAdmin().getId() == authentication.principal.id")
    public ResponseEntity<?> deleteOrganisation(@PathVariable Long id) {
        Optional<Organisation> organisation = organisationService.getOrganisationById(id);
        if (organisation.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        organisationService.deleteOrganisation(id);
        return ResponseEntity.ok().build();
    }

    private OrganisationDTO convertToDTO(Organisation organisation) {
        OrganisationDTO dto = new OrganisationDTO();
        dto.setId(organisation.getId());
        dto.setNom(organisation.getNom());
        dto.setDescription(organisation.getDescription());
        dto.setAdresseLegale(organisation.getAdresseLegale());
        dto.setNumeroIdentificationFiscale(organisation.getNumeroIdentificationFiscale());
        dto.setLogo(organisation.getLogo());
        dto.setContactPrincipal(organisation.getContactPrincipal());
        dto.setValidated(organisation.isValidated());
        if (organisation.getAdmin() != null) {
            dto.setAdminId(organisation.getAdmin().getId());
        }
        return dto;
    }
}