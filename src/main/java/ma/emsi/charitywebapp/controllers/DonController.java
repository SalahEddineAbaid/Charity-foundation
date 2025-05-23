package ma.emsi.charitywebapp.controllers;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import ma.emsi.charitywebapp.DTOs.DonDTO;
import ma.emsi.charitywebapp.entities.ActionCharite;
import ma.emsi.charitywebapp.entities.Don;
import ma.emsi.charitywebapp.entities.Utilisateur;
import ma.emsi.charitywebapp.services.ActionChariteService;
import ma.emsi.charitywebapp.services.DonService;
import ma.emsi.charitywebapp.services.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/dons")
public class DonController {

    private final DonService donService;
    private final UtilisateurService utilisateurService;
    private final ActionChariteService actionChariteService;

    @Autowired
    public DonController(DonService donService, UtilisateurService userService,
            ActionChariteService actionChariteService) {
        this.donService = donService;
        this.utilisateurService = userService;
        this.actionChariteService = actionChariteService;
    }

    @PostMapping("/submit")
    public String submitDonation(
            @RequestParam("actionId") Long actionId,
            @RequestParam("montant") Double montant,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            redirectAttributes.addFlashAttribute("infoMessage", "Veuillez vous connecter pour faire un don.");
            return "redirect:/login?redirectTo=/organisation/actions/public/" + actionId + "&montant=" + montant;
        }
        
        Utilisateur user = utilisateurService.getUserByEmail(authentication.getName()).orElse(null);
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Utilisateur introuvable");
            return "redirect:/organisation/actions/public/" + actionId;
        }
        
        ActionCharite action = actionChariteService.getActionChariteById(actionId).orElse(null);
        if (action == null) {
            redirectAttributes.addFlashAttribute("error", "Action introuvable");
            return "redirect:/organisation/actions/public/list";
        }
        
        Don don = new Don();
        don.setAction(action);
        don.setDonateur(user);
        don.setMontant(montant);
        don.setDateDon(LocalDateTime.now());
        don.setMethodePaiement("Carte bancaire");
        don.setStatutPaiement("COMPLETED");
        
        donService.saveDon(don);
        
        action.setMontantCollecte(action.getMontantCollecte() + montant);
        actionChariteService.updateActionCharite(action);
        
        redirectAttributes.addFlashAttribute("success", "Votre don de " + montant + "€ a été effectué avec succès. Merci pour votre générosité !");
        
        return "redirect:/organisation/actions/public/" + actionId;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDonById(@PathVariable Long id, Authentication authentication) {
        Optional<Don> don = donService.getDonById(id);
        if (don.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        // Vérification manuelle des autorisations
        boolean isAdmin = authentication.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            
        boolean isDonateur = false;
        try {
            Utilisateur user = utilisateurService.getUserByEmail(authentication.getName()).orElse(null);
            if (user != null && don.get().getDonateur() != null) {
                isDonateur = user.getId().equals(don.get().getDonateur().getId());
            }
        } catch (Exception e) {
            // Ignorer l'erreur et continuer avec isDonateur = false
        }
        
        // Vérifier que l'utilisateur est administrateur ou le donateur
        if (!isAdmin && !isDonateur) {
            return ResponseEntity.status(403).build();
        }
        
        return ResponseEntity.ok(convertToDTO(don.get()));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<DonDTO>> getAllDons() {
        List<Don> dons = donService.getAllDons();
        List<DonDTO> donDTOs = dons.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(donDTOs);
    }

    @GetMapping("/donateur/{donateurId}")
    @PreAuthorize("hasRole('ADMIN') or #donateurId == authentication.principal.id")
    public ResponseEntity<List<DonDTO>> getDonsByDonateur(@PathVariable Long donateurId) {
        Optional<Utilisateur> donateur = utilisateurService.getUserById(donateurId);
        if (donateur.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<Don> dons = donService.getDonsByDonateur(donateur.get());
        List<DonDTO> donDTOs = dons.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(donDTOs);
    }

    @GetMapping("/action/{actionId}")
    public ResponseEntity<List<DonDTO>> getDonsByAction(@PathVariable Long actionId) {
        Optional<ActionCharite> action = actionChariteService.getActionChariteById(actionId);
        if (action.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<Don> dons = donService.getDonsByAction(action.get());
        List<DonDTO> donDTOs = dons.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(donDTOs);
    }

    @GetMapping("/recu/{id}")
    public ResponseEntity<String> genererRecu(@PathVariable Long id, Authentication authentication) {
        Optional<Don> don = donService.getDonById(id);
        if (don.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        // Vérification manuelle des autorisations
        boolean isAdmin = authentication.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            
        boolean isDonateur = false;
        try {
            Utilisateur user = utilisateurService.getUserByEmail(authentication.getName()).orElse(null);
            if (user != null && don.get().getDonateur() != null) {
                isDonateur = user.getId().equals(don.get().getDonateur().getId());
            }
        } catch (Exception e) {
            // Ignorer l'erreur et continuer avec isDonateur = false
        }
        
        // Vérifier que l'utilisateur est administrateur ou le donateur
        if (!isAdmin && !isDonateur) {
            return ResponseEntity.status(403).build();
        }
        
        String recu = donService.genererRecu(id);
        return ResponseEntity.ok(recu);
    }

    @GetMapping("/recu-pdf/{id}")
    public ResponseEntity<byte[]> genererRecuPdf(@PathVariable Long id, Authentication authentication) {
        Optional<Don> donOpt = donService.getDonById(id);
        if (donOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Don don = donOpt.get();
        
        // Vérification manuelle des autorisations
        boolean isAdmin = authentication.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            
        boolean isDonateur = false;
        try {
            Utilisateur user = utilisateurService.getUserByEmail(authentication.getName()).orElse(null);
            if (user != null && don.getDonateur() != null) {
                isDonateur = user.getId().equals(don.getDonateur().getId());
            }
        } catch (Exception e) {
            // Ignorer l'erreur et continuer avec isDonateur = false
        }
        
        // Vérifier que l'utilisateur est administrateur ou le donateur
        if (!isAdmin && !isDonateur) {
            return ResponseEntity.status(403).build();
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, baos);
            document.open();

            document.add(new Paragraph("Reçu de don"));
            document.add(new Paragraph("Numéro du don : " + don.getId()));
            document.add(new Paragraph("Montant : " + don.getMontant() + " €"));
            document.add(new Paragraph("Date : " + don.getDateDon()));
            document.add(
                    new Paragraph("Donateur : " + don.getDonateur().getNom() + " " + don.getDonateur().getPrenom()));
            document.add(new Paragraph("Action : " + (don.getAction() != null ? don.getAction().getTitre() : "")));
            document.add(new Paragraph("Merci pour votre générosité !"));

            document.close();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=recu-don-" + don.getId() + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(baos.toByteArray());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateDon(@PathVariable Long id, @RequestBody DonDTO donDTO) {
        Optional<Don> optionalDon = donService.getDonById(id);
        if (optionalDon.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Don don = optionalDon.get();
        don.setMontant(donDTO.getMontant());
        don.setMethodePaiement(donDTO.getMethodePaiement());
        don.setStatutPaiement(donDTO.getStatutPaiement());
        don.setReferencePaiement(donDTO.getReferencePaiement());

        Don updatedDon = donService.updateDon(don);
        return ResponseEntity.ok(convertToDTO(updatedDon));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteDon(@PathVariable Long id) {
        Optional<Don> don = donService.getDonById(id);
        if (don.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        donService.deleteDon(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/dons")
    @PreAuthorize("hasAnyRole('UTILISATEUR','ORGANISATION')")
    public String userDons(Model model, org.springframework.security.core.Authentication authentication) {
        Utilisateur user = utilisateurService.getUserByEmail(authentication.getName()).orElseThrow();
        List<Don> dons = donService.getDonsByDonateur(user);
        model.addAttribute("dons", dons);
        return "user/dons";
    }

    private DonDTO convertToDTO(Don don) {
        DonDTO dto = new DonDTO();
        dto.setId(don.getId());
        dto.setMontant(don.getMontant());
        dto.setMethodePaiement(don.getMethodePaiement());
        dto.setStatutPaiement(don.getStatutPaiement());
        dto.setReferencePaiement(don.getReferencePaiement());
        dto.setDateDon(don.getDateDon());

        if (don.getDonateur() != null) {
            dto.setDonateurId(don.getDonateur().getId());
            dto.setDonateurNom(don.getDonateur().getNom() + " " + don.getDonateur().getPrenom());
        }

        if (don.getAction() != null) {
            dto.setActionId(don.getAction().getId());
            dto.setActionTitre(don.getAction().getTitre());
        }

        return dto;
    }
}