package ma.emsi.charitywebapp.services;

import ma.emsi.charitywebapp.entities.ActionCharite;
import ma.emsi.charitywebapp.entities.Notification;
import ma.emsi.charitywebapp.entities.Utilisateur;
import ma.emsi.charitywebapp.repositories.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NotificationServiceImp implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Autowired
    public NotificationServiceImp(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public Notification saveNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

    @Override
    public Notification updateNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

    @Override
    public Optional<Notification> getNotificationById(Long id) {
        return notificationRepository.findById(id);
    }

    @Override
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    @Override
    public List<Notification> getNotificationsByUtilisateur(Utilisateur utilisateur) {
        return notificationRepository.findByUtilisateurOrderByDateCreationDesc(utilisateur);
    }

    @Override
    public List<Notification> getUnreadNotificationsByUtilisateur(Utilisateur utilisateur) {
        return notificationRepository.findByUtilisateurAndLu(utilisateur, false);
    }

    @Override
    public List<Notification> getNotificationsByAction(ActionCharite action) {
        return notificationRepository.findByAction(action);
    }

    @Override
    public void deleteNotification(Long id) {
        notificationRepository.deleteById(id);
    }

    @Override
    public void markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification non trouvée"));
        notification.setLu(true);
        notificationRepository.save(notification);
    }

    @Override
    public void markAllAsRead(Utilisateur utilisateur) {
        List<Notification> unreadNotifications = notificationRepository.findByUtilisateurAndLu(utilisateur, false);
        for (Notification notification : unreadNotifications) {
            notification.setLu(true);
            notificationRepository.save(notification);
        }
    }
}