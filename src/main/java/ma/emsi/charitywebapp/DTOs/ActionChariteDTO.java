package ma.emsi.charitywebapp.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActionChariteDTO {
    private Long id;
    private String titre;
    private String description;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private String lieu;
    private double objectifCollecte;
    private double montantCollecte;
    private String statut;
    private Long categorieId;
    private String categorieNom;
    private Long organisationId;
    private String organisationNom;

    public Long getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(Long organisationId) {
        this.organisationId = organisationId;
    }

    public Long getId() {
        return id;
    }

    public Long getCategorieId() {
        return categorieId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCategorieId(Long categorieId) {
        this.categorieId = categorieId;
    }

    public String getTitre() {
        return titre;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getDateDebut() {
        return dateDebut;
    }

    public LocalDateTime getDateFin() {
        return dateFin;
    }

    public String getLieu() {
        return lieu;
    }

    public double getObjectifCollecte() {
        return objectifCollecte;
    }

    public double getMontantCollecte() {
        return montantCollecte;
    }

    public String getStatut() {
        return statut;
    }

    public String getCategorieNom() {
        return categorieNom;
    }

    public String getOrganisationNom() {
        return organisationNom;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDateDebut(LocalDateTime dateDebut) {
        this.dateDebut = dateDebut;
    }

    public void setDateFin(LocalDateTime dateFin) {
        this.dateFin = dateFin;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public void setObjectifCollecte(double objectifCollecte) {
        this.objectifCollecte = objectifCollecte;
    }

    public void setMontantCollecte(double montantCollecte) {
        this.montantCollecte = montantCollecte;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public void setCategorieNom(String categorieNom) {
        this.categorieNom = categorieNom;
    }

    public void setOrganisationNom(String organisationNom) {
        this.organisationNom = organisationNom;
    }
}