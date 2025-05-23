package ma.emsi.charitywebapp.repositories;

import ma.emsi.charitywebapp.entities.ActionCharite;
import ma.emsi.charitywebapp.entities.Categorie;
import ma.emsi.charitywebapp.entities.Organisation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface ActionChariteRepository extends JpaRepository<ActionCharite, Long> {
    List<ActionCharite> findByOrganisation(Organisation organisation);

    List<ActionCharite> findByOrganisationAndStatut(Organisation organisation, String statut);

    List<ActionCharite> findByCategorie(Categorie categorie);

    List<ActionCharite> findByStatut(String statut);

    List<ActionCharite> findByDateDebutAfter(LocalDateTime date);

    List<ActionCharite> findByTitreContainingIgnoreCase(String keyword);

    List<ActionCharite> findTop10ByOrderByMontantCollecteDesc();

    long countByStatut(String statut);
    
    @Query("SELECT a FROM ActionCharite a WHERE a.organisation IS NOT NULL")
    List<ActionCharite> findAllWithOrganisation();

    @Query("SELECT a FROM ActionCharite a WHERE a.organisation.id = :organisationId")
    List<ActionCharite> findByOrganisationIdDirect(Long organisationId);
}