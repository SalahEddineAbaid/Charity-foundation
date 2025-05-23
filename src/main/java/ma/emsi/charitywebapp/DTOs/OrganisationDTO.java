package ma.emsi.charitywebapp.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrganisationDTO {
    private Long id;
    private String nom;
    private String description;
    private String adresseLegale;
    private String numeroIdentificationFiscale;
    private String logo;
    private String contactPrincipal;
    private boolean validated;
    private Long adminId;

    public Long getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getDescription() {
        return description;
    }

    public Long getAdminId() {
        return adminId;
    }

    public boolean isValidated() {
        return validated;
    }

    public String getContactPrincipal() {
        return contactPrincipal;
    }

    public String getLogo() {
        return logo;
    }

    public String getNumeroIdentificationFiscale() {
        return numeroIdentificationFiscale;
    }

    public String getAdresseLegale() {
        return adresseLegale;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setAdresseLegale(String adresseLegale) {
        this.adresseLegale = adresseLegale;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    public void setValidated(boolean validated) {
        this.validated = validated;
    }

    public void setContactPrincipal(String contactPrincipal) {
        this.contactPrincipal = contactPrincipal;
    }

    public void setNumeroIdentificationFiscale(String numeroIdentificationFiscale) {
        this.numeroIdentificationFiscale = numeroIdentificationFiscale;
    }
}