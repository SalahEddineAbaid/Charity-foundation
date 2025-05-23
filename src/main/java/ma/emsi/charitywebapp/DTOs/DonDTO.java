package ma.emsi.charitywebapp.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DonDTO {
    private Long id;
    private double montant;
    private String methodePaiement;
    private String statutPaiement;
    private String referencePaiement;
    private LocalDateTime dateDon;
    private Long donateurId;
    private String donateurNom;
    private Long actionId;
    private String actionTitre;

    public Long getId() {
        return id;
    }

    public double getMontant() {
        return montant;
    }

    public String getMethodePaiement() {
        return methodePaiement;
    }

    public String getStatutPaiement() {
        return statutPaiement;
    }

    public String getReferencePaiement() {
        return referencePaiement;
    }

    public LocalDateTime getDateDon() {
        return dateDon;
    }

    public Long getDonateurId() {
        return donateurId;
    }

    public Long getActionId() {
        return actionId;
    }

    public String getActionTitre() {
        return actionTitre;
    }

    public String getDonateurNom() {
        return donateurNom;
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

    public void setDonateurId(Long donateurId) {
        this.donateurId = donateurId;
    }

    public void setDateDon(LocalDateTime dateDon) {
        this.dateDon = dateDon;
    }

    public void setDonateurNom(String donateurNom) {
        this.donateurNom = donateurNom;
    }

    public void setActionId(Long actionId) {
        this.actionId = actionId;
    }

    public void setActionTitre(String actionTitre) {
        this.actionTitre = actionTitre;
    }
}