package ma.emsi.charitywebapp.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "dons")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Don {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double montant;
    private String methodePaiement;
    private String statutPaiement;
    private String referencePaiement;
    private LocalDateTime dateDon = LocalDateTime.now();
    private LocalDateTime dateCreation = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "donateur_id")
    private Utilisateur donateur;

    @ManyToOne
    @JoinColumn(name = "action_id")
    private ActionCharite action;

    @Column(name = "stripe_session_id")
    private String stripeSessionId;

    public String genererRecu() {
        return "Reçu pour don #" + id + " - Montant: " + montant + "€";
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setMontant(double montant) {
        this.montant = montant;
    }

    public void setMethodePaiement(String methodePaiement) {
        this.methodePaiement = methodePaiement;
    }

    public void setStatutPaiement(String statutPaiement) {
        this.statutPaiement = statutPaiement;
    }

    public void setReferencePaiement(String referencePaiement) {
        this.referencePaiement = referencePaiement;
    }

    public void setDateDon(LocalDateTime dateDon) {
        this.dateDon = dateDon;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public void setDonateur(Utilisateur donateur) {
        this.donateur = donateur;
    }

    public void setAction(ActionCharite action) {
        this.action = action;
    }

    public void setStripeSessionId(String stripeSessionId) {
        this.stripeSessionId = stripeSessionId;
    }

    public Long getId() {
        return id;
    }

    public double getMontant() {
        return montant;
    }

    public String getMethodePaiement() {
        return methodePaiement;
    }

    public String getReferencePaiement() {
        return referencePaiement;
    }

    public String getStatutPaiement() {
        return statutPaiement;
    }

    public LocalDateTime getDateDon() {
        return dateDon;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public Utilisateur getDonateur() {
        return donateur;
    }

    public ActionCharite getAction() {
        return action;
    }

    public String getStripeSessionId() {
        return stripeSessionId;
    }
}