package ma.emsi.charitywebapp.repositories;

import ma.emsi.charitywebapp.entities.ActionCharite;
import ma.emsi.charitywebapp.entities.Notification;
import ma.emsi.charitywebapp.entities.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUtilisateurOrderByDateCreationDesc(Utilisateur utilisateur);
    List<Notification> findByUtilisateurAndLu(Utilisateur utilisateur, boolean lu);
    List<Notification> findByAction(ActionCharite action);
}