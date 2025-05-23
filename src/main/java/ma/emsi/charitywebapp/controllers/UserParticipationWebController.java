package ma.emsi.charitywebapp.controllers;

import ma.emsi.charitywebapp.entities.ActionCharite;
import ma.emsi.charitywebapp.entities.Participation;
import ma.emsi.charitywebapp.entities.Utilisateur;
import ma.emsi.charitywebapp.services.ActionChariteService;
import ma.emsi.charitywebapp.services.ParticipationService;
import ma.emsi.charitywebapp.services.UtilisateurService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/participations")
public class UserParticipationWebController {

    @Autowired
    private ParticipationService participationService;
    
    @Autowired
    private UtilisateurService utilisateurService;
    
    @Autowired
    private ActionChariteService actionChariteService;
    
    /**
     * Affiche la page de toutes les participations de l'utilisateur connecté
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('UTILISATEUR','ADMIN')")
    public String listParticipations(Model model, Authentication authentication) {
        Utilisateur user = utilisateurService.getUserByEmail(authentication.getName()).orElseThrow();
        List<Participation> participations = participationService.getParticipationsByUtilisateur(user);
        
        model.addAttribute("participations", participations);
        return "user/participations";
    }
    
    /**
     * S'inscrire à une action
     */
    @GetMapping("/inscrire/action/{actionId}")
    @PreAuthorize("hasAnyRole('UTILISATEUR','ADMIN')")
    public String inscrireAction(@PathVariable Long actionId, Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            Utilisateur user = utilisateurService.getUserByEmail(authentication.getName()).orElseThrow();
            ActionCharite action = actionChariteService.getActionChariteById(actionId).orElseThrow();
            
            // Vérifier si l'utilisateur est déjà inscrit
            Optional<Participation> existingParticipation = 
                participationService.getParticipationByUtilisateurAndAction(user, action);
            
            if (existingParticipation.isPresent()) {
                // Si la participation existe mais est annulée, la réactiver
                if ("Annulé".equals(existingParticipation.get().getStatut())) {
                    existingParticipation.get().setStatut("Inscrit");
                    existingParticipation.get().setDateParticipation(LocalDateTime.now());
                    participationService.updateParticipation(existingParticipation.get());
                    return "redirect:/organisation/actions/public/" + actionId + "?success=Vous êtes à nouveau inscrit à cette action";
                } else {
                    return "redirect:/organisation/actions/public/" + actionId + "?warning=Vous êtes déjà inscrit à cette action";
                }
            } else {
                // Créer une nouvelle participation
                Participation participation = new Participation();
                participation.setUtilisateur(user);
                participation.setAction(action);
                participation.setStatut("Inscrit");
                participation.setDateParticipation(LocalDateTime.now());
                participationService.saveParticipation(participation);
                return "redirect:/organisation/actions/public/" + actionId + "?success=Inscription réussie. Consultez vos participations pour plus de détails.";
            }
        } catch (Exception e) {
            return "redirect:/organisation/actions/public/" + actionId + "?error=" + e.getMessage();
        }
    }
    
    /**
     * Réinscrire à une participation annulée
     */
    @PostMapping("/inscrire/{id}")
    @PreAuthorize("hasAnyRole('UTILISATEUR','ADMIN')")
    public String reinscrire(@PathVariable Long id, Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            Utilisateur user = utilisateurService.getUserByEmail(authentication.getName()).orElseThrow();
            Participation participation = participationService.getParticipationById(id).orElseThrow();
            
            // Vérifier que la participation appartient bien à l'utilisateur connecté
            if (participation.getUtilisateur().getId().equals(user.getId())) {
                participation.setStatut("Inscrit");
                participation.setDateParticipation(LocalDateTime.now());
                participationService.updateParticipation(participation);
                redirectAttributes.addFlashAttribute("success", "Vous êtes à nouveau inscrit à cette action");
            } else {
                redirectAttributes.addFlashAttribute("error", "Vous n'êtes pas autorisé à modifier cette participation");
            }
            
            return "redirect:/participations";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Une erreur s'est produite: " + e.getMessage());
            return "redirect:/participations";
        }
    }
    
    /**
     * Confirmer une participation
     */
    @PostMapping("/confirmer/{id}")
    @PreAuthorize("hasAnyRole('UTILISATEUR','ADMIN')")
    public String confirmerParticipation(@PathVariable Long id, Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            Utilisateur user = utilisateurService.getUserByEmail(authentication.getName()).orElseThrow();
            Participation participation = participationService.getParticipationById(id).orElseThrow();
            
            // Vérifier que la participation appartient bien à l'utilisateur connecté
            if (participation.getUtilisateur().getId().equals(user.getId())) {
                participation.setStatut("Confirmé");
                participationService.updateParticipation(participation);
                redirectAttributes.addFlashAttribute("success", "Participation confirmée");
            } else {
                redirectAttributes.addFlashAttribute("error", "Vous n'êtes pas autorisé à modifier cette participation");
            }
            
            return "redirect:/participations";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Une erreur s'est produite: " + e.getMessage());
            return "redirect:/participations";
        }
    }
    
    /**
     * Annuler une participation
     */
    @PostMapping("/annuler/{id}")
    @PreAuthorize("hasAnyRole('UTILISATEUR','ADMIN')")
    public String annulerParticipation(@PathVariable Long id, Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            Utilisateur user = utilisateurService.getUserByEmail(authentication.getName()).orElseThrow();
            Participation participation = participationService.getParticipationById(id).orElseThrow();
            
            // Vérifier que la participation appartient bien à l'utilisateur connecté
            if (participation.getUtilisateur().getId().equals(user.getId())) {
                participation.setStatut("Annulé");
                participationService.updateParticipation(participation);
                redirectAttributes.addFlashAttribute("success", "Participation annulée");
            } else {
                redirectAttributes.addFlashAttribute("error", "Vous n'êtes pas autorisé à modifier cette participation");
            }
            
            return "redirect:/participations";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Une erreur s'est produite: " + e.getMessage());
            return "redirect:/participations";
        }
    }
} 