package ma.emsi.charitywebapp.controllers;

import ma.emsi.charitywebapp.entities.ActionCharite;
import ma.emsi.charitywebapp.entities.Notification;
import ma.emsi.charitywebapp.entities.Utilisateur;
import ma.emsi.charitywebapp.services.ActionChariteService;
import ma.emsi.charitywebapp.services.NotificationService;
import ma.emsi.charitywebapp.services.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final UtilisateurService userService;
    private final ActionChariteService actionChariteService;

    @Autowired
    public NotificationController(NotificationService notificationService,
                                  UtilisateurService userService,
                                  ActionChariteService actionChariteService) {
        this.notificationService = notificationService;
        this.userService = userService;
        this.actionChariteService = actionChariteService;
    }

    @PostMapping
    public ResponseEntity<Notification> createNotification(@RequestBody Notification notification) {
        Notification savedNotification = notificationService.saveNotification(notification);
        return new ResponseEntity<>(savedNotification, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Notification>> getAllNotifications() {
        List<Notification> notifications = notificationService.getAllNotifications();
        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Notification> getNotificationById(@PathVariable Long id) {
        Optional<Notification> notification = notificationService.getNotificationById(id);
        return notification.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getNotificationsByUser(@PathVariable Long userId) {
        Optional<Utilisateur> user = userService.getUserById(userId);
        if (user.isPresent()) {
            List<Notification> notifications = notificationService.getNotificationsByUtilisateur(user.get());
            return new ResponseEntity<>(notifications, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<Notification>> getUnreadNotificationsByUser(@PathVariable Long userId) {
        Optional<Utilisateur> user = userService.getUserById(userId);
        if (user.isPresent()) {
            List<Notification> notifications = notificationService.getUnreadNotificationsByUtilisateur(user.get());
            return new ResponseEntity<>(notifications, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/action/{actionId}")
    public ResponseEntity<List<Notification>> getNotificationsByAction(@PathVariable Long actionId) {
        Optional<ActionCharite> action = actionChariteService.getActionById(actionId);
        if (action.isPresent()) {
            List<Notification> notifications = notificationService.getNotificationsByAction(action.get());
            return new ResponseEntity<>(notifications, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Notification> updateNotification(@PathVariable Long id, @RequestBody Notification notification) {
        Optional<Notification> existingNotification = notificationService.getNotificationById(id);
        if (existingNotification.isPresent()) {
            notification.setId(id);
            Notification updatedNotification = notificationService.updateNotification(notification);
            return new ResponseEntity<>(updatedNotification, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        Optional<Notification> notification = notificationService.getNotificationById(id);
        if (notification.isPresent()) {
            notificationService.markAsRead(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/user/{userId}/read-all")
    public ResponseEntity<Void> markAllAsRead(@PathVariable Long userId) {
        Optional<Utilisateur> user = userService.getUserById(userId);
        if (user.isPresent()) {
            notificationService.markAllAsRead(user.get());
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        Optional<Notification> notification = notificationService.getNotificationById(id);
        if (notification.isPresent()) {
            notificationService.deleteNotification(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
