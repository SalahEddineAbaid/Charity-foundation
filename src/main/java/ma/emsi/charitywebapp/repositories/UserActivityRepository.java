package ma.emsi.charitywebapp.repositories;

import ma.emsi.charitywebapp.entities.UserActivity;
import ma.emsi.charitywebapp.entities.Utilisateur;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserActivityRepository extends JpaRepository<UserActivity, Long> {
    List<UserActivity> findByUtilisateurOrderByDateDesc(Utilisateur utilisateur, Pageable pageable);
}
