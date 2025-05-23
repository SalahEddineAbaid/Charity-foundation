package ma.emsi.charitywebapp.repositories;

import ma.emsi.charitywebapp.entities.ActionCharite;
import ma.emsi.charitywebapp.entities.Don;
import ma.emsi.charitywebapp.entities.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.LinkedHashMap;

@Repository
public interface DonRepository extends JpaRepository<Don, Long> {
    List<Don> findByDonateur(Utilisateur donateur);

    List<Don> findByAction(ActionCharite action);

    List<Don> findByStatutPaiement(String statutPaiement);

    Optional<Don> findByStripeSessionId(String stripeSessionId);

    @Query("SELECT SUM(d.montant) FROM Don d WHERE d.action = :action AND d.statutPaiement = 'COMPLETED'")
    Double sumMontantByAction(@Param("action") ActionCharite action);

    @Query("SELECT SUM(d.montant) FROM Don d WHERE d.donateur = :donateur")
    Double sumMontantByDonateur(@Param("donateur") Utilisateur donateur);

    @Query("SELECT SUM(d.montant) FROM Don d WHERE d.statutPaiement = 'COMPLETED'")
    Double sumTotalMontant();
    
    @Query("SELECT SUM(d.montant) FROM Don d")
    Double sumMontant();
    
    @Query("SELECT CAST(SUM(d.montant) AS java.math.BigDecimal) FROM Don d")
    BigDecimal sumTotalAmount();

    long countByStatutPaiement(String statutPaiement);

    @Query("SELECT FUNCTION('DATE_FORMAT', d.dateDon, '%Y-%m') as mois, SUM(d.montant) as total " +
            "FROM Don d GROUP BY mois ORDER BY mois")
    List<Object[]> sumMontantGroupByMonthRaw();

    default Map<String, Double> sumMontantGroupByMonth() {
        Map<String, Double> result = new LinkedHashMap<>();
        for (Object[] row : sumMontantGroupByMonthRaw()) {
            result.put((String) row[0], ((Number) row[1]).doubleValue());
        }
        return result;
    }
}