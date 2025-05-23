package ma.emsi.charitywebapp.controllers;

import ma.emsi.charitywebapp.entities.Organisation;
import ma.emsi.charitywebapp.entities.Utilisateur;
import ma.emsi.charitywebapp.services.NotificationService;
import ma.emsi.charitywebapp.services.OrganisationService;
import ma.emsi.charitywebapp.services.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Optional;

/**
 * Contrôleur global qui expose des attributs communs à toutes les pages
 */
@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UtilisateurService utilisateurService;

    @Autowired
    private OrganisationService organisationService;

    @ModelAttribute
    public void addGlobalAttributes(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser")) {
            Optional<Utilisateur> utilisateur = utilisateurService.getUserByEmail(authentication.getName());
            
            if (utilisateur.isPresent()) {
                int unreadNotificationsCount = notificationService.getUnreadNotificationsByUtilisateur(utilisateur.get()).size();
                model.addAttribute("globalUnreadNotifications", unreadNotificationsCount);
                
                // Vérifier si l'utilisateur est administrateur d'une organisation
                if (utilisateur.get().getRoles().contains("ROLE_ORGANISATION")) {
                    Optional<Organisation> organisation = organisationService.getOrganisationsByAdmin(utilisateur.get()).stream().findFirst();
                    if (organisation.isPresent()) {
                        model.addAttribute("globalOrganisation", organisation.get());
                    }
                }
            }
        }
    }

    /**
     * Expose le nombre de notifications non lues pour l'utilisateur connecté
     */
    @ModelAttribute("unreadCount")
    public Integer getUnreadNotificationCount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        // Vérifier si un utilisateur est connecté
        if (authentication != null && authentication.isAuthenticated() && 
            !authentication.getName().equals("anonymousUser")) {
                
            try {
                // Récupérer l'utilisateur
                Utilisateur user = utilisateurService.getUserByEmail(authentication.getName()).orElse(null);
                
                if (user != null) {
                    // Récupérer le nombre de notifications non lues
                    return notificationService.getUnreadNotificationsByUtilisateur(user).size();
                }
            } catch (Exception e) {
                // En cas d'erreur, on retourne simplement 0
                return 0;
            }
        }
        
        return 0;
    }
} 