package ma.emsi.charitywebapp.services;

import ma.emsi.charitywebapp.entities.ActionCharite;
import ma.emsi.charitywebapp.entities.Notification;
import ma.emsi.charitywebapp.entities.Utilisateur;

import java.util.List;
import java.util.Optional;

public interface NotificationService {
    Notification saveNotification(Notification notification);

    Notification updateNotification(Notification notification);

    Optional<Notification> getNotificationById(Long id);

    List<Notification> getAllNotifications();

    List<Notification> getNotificationsByUtilisateur(Utilisateur utilisateur);

    List<Notification> getUnreadNotificationsByUtilisateur(Utilisateur utilisateur);

    List<Notification> getNotificationsByAction(ActionCharite action);

    void deleteNotification(Long id);

    void markAsRead(Long id);

    void markAllAsRead(Utilisateur utilisateur);
}