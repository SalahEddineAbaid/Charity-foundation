package ma.emsi.charitywebapp.repositories;

import ma.emsi.charitywebapp.entities.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    Optional<Utilisateur> findByEmail(String email);

    boolean existsByEmail(String email);
    
    @Query("SELECT COUNT(u) FROM Utilisateur u WHERE u.enabled = true AND :role MEMBER OF u.roles")
    long countByRole(@Param("role") String role);
    
    @Query("SELECT u FROM Utilisateur u WHERE :role MEMBER OF u.roles")
    List<Utilisateur> findAllByRole(@Param("role") String role);
    
    @Query("SELECT COUNT(DISTINCT u) FROM Utilisateur u JOIN u.dons d WHERE :role MEMBER OF u.roles AND u.enabled = true")
    long countByRoleAndHasMadeDonation(@Param("role") String role, boolean hasMadeDonation);
}