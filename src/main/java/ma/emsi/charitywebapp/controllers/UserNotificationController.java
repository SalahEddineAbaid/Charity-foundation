package ma.emsi.charitywebapp.controllers;

import ma.emsi.charitywebapp.entities.Notification;
import ma.emsi.charitywebapp.entities.Utilisateur;
import ma.emsi.charitywebapp.services.NotificationService;
import ma.emsi.charitywebapp.services.UtilisateurService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserNotificationController {

    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private UtilisateurService utilisateurService;
    
    /**
     * Affiche la page des notifications de l'utilisateur
     */
    @GetMapping("/notifications")
    public String showNotifications(
            @RequestParam(required = false) String type,
            Model model, 
            Authentication authentication) {
        
        Utilisateur user = utilisateurService.getUserByEmail(authentication.getName()).orElseThrow();
        
        // Récupérer toutes les notifications de l'utilisateur
        List<Notification> notifications;
        if (type != null && !type.isEmpty()) {
            // Si un filtre est spécifié, on filtrerait par type (à implémenter si la notification avait un champ type)
            // Pour l'exemple, on renvoie toutes les notifications
            notifications = notificationService.getNotificationsByUtilisateur(user);
        } else {
            notifications = notificationService.getNotificationsByUtilisateur(user);
        }
        
        // Compter les notifications non lues
        int unreadCount = notificationService.getUnreadNotificationsByUtilisateur(user).size();
        
        // Grouper les notifications par type (exemple: don, participation, système)
        Map<String, Long> notificationsByType = new HashMap<>();
        notificationsByType.put("Tous", (long) notifications.size());
        notificationsByType.put("Non lus", (long) unreadCount);
        
        // On pourrait ajouter d'autres types si l'entité Notification avait un champ 'type'
        
        model.addAttribute("notifications", notifications);
        model.addAttribute("notificationsByType", notificationsByType);
        model.addAttribute("unreadCount", unreadCount);
        model.addAttribute("selectedType", type != null ? type : "Tous");
        model.addAttribute("currentPath", "/user/notifications");
        
        return "user/notifications";
    }
    
    /**
     * Marquer une notification comme lue
     */
    @PostMapping("/notifications/{id}/mark-read")
    public String markAsRead(@PathVariable Long id, Authentication authentication, RedirectAttributes redirectAttributes) {
        Utilisateur user = utilisateurService.getUserByEmail(authentication.getName()).orElseThrow();
        
        try {
            // Vérifier que la notification appartient bien à l'utilisateur connecté
            notificationService.getNotificationById(id).ifPresent(notification -> {
                if (notification.getUtilisateur().getId().equals(user.getId())) {
                    notificationService.markAsRead(id);
                }
            });
            
            redirectAttributes.addFlashAttribute("success", "Notification marquée comme lue");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la mise à jour de la notification");
        }
        
        return "redirect:/user/notifications";
    }
    
    /**
     * Marquer une notification comme non lue
     */
    @PostMapping("/notifications/{id}/mark-unread")
    public String markAsUnread(@PathVariable Long id, Authentication authentication, RedirectAttributes redirectAttributes) {
        Utilisateur user = utilisateurService.getUserByEmail(authentication.getName()).orElseThrow();
        
        try {
            // Cette méthode nécessiterait l'ajout d'une méthode markAsUnread dans NotificationService
            // Pour l'exemple, on signale simplement que cette fonctionnalité serait à implémenter
            redirectAttributes.addFlashAttribute("info", "Fonctionnalité à implémenter");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la mise à jour de la notification");
        }
        
        return "redirect:/user/notifications";
    }
    
    /**
     * Marquer toutes les notifications comme lues
     */
    @PostMapping("/notifications/mark-all-read")
    public String markAllAsRead(Authentication authentication, RedirectAttributes redirectAttributes) {
        Utilisateur user = utilisateurService.getUserByEmail(authentication.getName()).orElseThrow();
        
        try {
            notificationService.markAllAsRead(user);
            redirectAttributes.addFlashAttribute("success", "Toutes les notifications ont été marquées comme lues");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la mise à jour des notifications");
        }
        
        return "redirect:/user/notifications";
    }
    
    /**
     * Supprimer une notification
     */
    @PostMapping("/notifications/{id}/delete")
    public String deleteNotification(@PathVariable Long id, Authentication authentication, RedirectAttributes redirectAttributes) {
        Utilisateur user = utilisateurService.getUserByEmail(authentication.getName()).orElseThrow();
        
        try {
            // Vérifier que la notification appartient bien à l'utilisateur connecté
            notificationService.getNotificationById(id).ifPresent(notification -> {
                if (notification.getUtilisateur().getId().equals(user.getId())) {
                    notificationService.deleteNotification(id);
                }
            });
            
            redirectAttributes.addFlashAttribute("success", "Notification supprimée");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression de la notification");
        }
        
        return "redirect:/user/notifications";
    }
} 