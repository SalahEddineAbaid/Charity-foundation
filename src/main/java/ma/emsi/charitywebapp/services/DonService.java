package ma.emsi.charitywebapp.services;

import ma.emsi.charitywebapp.entities.ActionCharite;
import ma.emsi.charitywebapp.entities.Don;
import ma.emsi.charitywebapp.entities.Utilisateur;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface DonService {
    Don saveDon(Don don);

    Don updateDon(Don don);

    Optional<Don> getDonById(Long id);

    List<Don> getAllDons();

    List<Don> getDonsByDonateur(Utilisateur donateur);

    List<Don> getDonsByAction(ActionCharite action);

    Double getTotalDonsByAction(ActionCharite action);

    Double getTotalDonsByDonateur(Utilisateur donateur);

    void deleteDon(Long id);

    String genererRecu(Long donId);

    Don getDonByStripeSessionId(String sessionId);

    long countAll();

    double sumMontant();

    Map<String, Double> getDonsParMois();

    BigDecimal getTotalDonationAmount();

    Map<String, BigDecimal> getDonationsByMonth();
    public List<Don> getDonsByUtilisateur(Utilisateur utilisateur);
}