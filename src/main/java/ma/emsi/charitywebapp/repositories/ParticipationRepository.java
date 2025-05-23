package ma.emsi.charitywebapp.repositories;

import ma.emsi.charitywebapp.entities.ActionCharite;
import ma.emsi.charitywebapp.entities.Participation;
import ma.emsi.charitywebapp.entities.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipationRepository extends JpaRepository<Participation, Long> {
    List<Participation> findByUtilisateur(Utilisateur utilisateur);
    List<Participation> findByAction(ActionCharite action);
    Optional<Participation> findByUtilisateurAndAction(Utilisateur utilisateur, ActionCharite action);
    long countByAction(ActionCharite action);
    @Query("SELECT COUNT(p) FROM Participation p WHERE p.action.organisation.id = :organisationId")
    int countByActionOrganisationId(@Param("organisationId") Long organisationId);
}
