package ma.emsi.charitywebapp.controllers;

import ma.emsi.charitywebapp.entities.ActionCharite;
import ma.emsi.charitywebapp.entities.Don;
import ma.emsi.charitywebapp.entities.Participation;
import ma.emsi.charitywebapp.entities.Utilisateur;
import ma.emsi.charitywebapp.services.ActionChariteService;
import ma.emsi.charitywebapp.services.DonService;
import ma.emsi.charitywebapp.services.ParticipationService;
import ma.emsi.charitywebapp.services.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UtilisateurService utilisateurService;
    private final DonService donService;
    private final ActionChariteService actionChariteService;
    private final ParticipationService participationService;

    @Autowired
    public UserController(UtilisateurService utilisateurService, 
                         DonService donService, 
                         ActionChariteService actionChariteService,
                         ParticipationService participationService) {
        this.utilisateurService = utilisateurService;
        this.donService = donService;
        this.actionChariteService = actionChariteService;
        this.participationService = participationService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        // Récupérer l'utilisateur connecté
        Utilisateur user = utilisateurService.getUserByEmail(authentication.getName()).orElseThrow();
        model.addAttribute("user", user);
        
        // Récupérer les dons de l'utilisateur
        List<Don> userDons = donService.getDonsByDonateur(user);
        
        // Calculer les statistiques de dons
        double totalDonated = userDons.stream().mapToDouble(Don::getMontant).sum();
        int donCount = userDons.size();
        
        // Obtenir le nombre d'actions uniques auxquelles l'utilisateur a contribué
        long uniqueActionCount = userDons.stream()
                .map(don -> don.getAction().getId())
                .distinct()
                .count();
        
        // Récupérer les participations de l'utilisateur
        List<Participation> participations = participationService.getParticipationsByUtilisateur(user);
        int participationCount = participations.size();
        
        // Ajouter les statistiques au modèle
        model.addAttribute("totalDonated", totalDonated);
        model.addAttribute("donCount", donCount);
        model.addAttribute("uniqueActionCount", uniqueActionCount);
        model.addAttribute("participationCount", participationCount);
        
        // Récupérer quelques actions recommandées (ici simplement les plus récentes)
        List<ActionCharite> recommendedActions = actionChariteService.getAllActionsCharite();
        if (recommendedActions.size() > 3) {
            recommendedActions = recommendedActions.subList(0, 3);
        }
        model.addAttribute("recommendedActions", recommendedActions);
        
        // Passer le chemin actuel pour marquer le lien actif dans la barre latérale
        model.addAttribute("currentPath", "/");
        
        return "user/dashboard";
    }
    
    // La méthode userDons a été supprimée pour éviter le conflit avec UserWebController
} 