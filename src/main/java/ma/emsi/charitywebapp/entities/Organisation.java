package ma.emsi.charitywebapp.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "organisations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Organisation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @Column(columnDefinition = "TEXT")
    @NotBlank(message = "La description est obligatoire")
    private String description;

    @NotBlank(message = "L'adresse légale est obligatoire")
    private String adresseLegale;

    @NotBlank(message = "Le numéro d'identification fiscale est obligatoire")
    private String numeroIdentificationFiscale;

    private String logo;

    @NotBlank(message = "Le contact principal est obligatoire")
    private String contactPrincipal;

    private boolean validated = false;
    private LocalDateTime dateCreation = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private Utilisateur admin;

    @OneToMany(mappedBy = "organisation")
    private Set<ActionCharite> actions = new HashSet<>();

    private boolean enabled = true;

    private String email;

    public Long getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getDescription() {
        return description;
    }

    public String getAdresseLegale() {
        return adresseLegale;
    }

    public String getNumeroIdentificationFiscale() {
        return numeroIdentificationFiscale;
    }

    public String getLogo() {
        return logo;
    }

    public String getContactPrincipal() {
        return contactPrincipal;
    }

    public boolean isValidated() {
        return validated;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public Utilisateur getAdmin() {
        return admin;
    }

    public Set<ActionCharite> getActions() {
        return actions;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getEmail() {
        return email;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setAdresseLegale(String adresseLegale) {
        this.adresseLegale = adresseLegale;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setActions(Set<ActionCharite> actions) {
        this.actions = actions;
    }

    public void setAdmin(Utilisateur admin) {
        this.admin = admin;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public void setValidated(boolean validated) {
        this.validated = validated;
    }

    public void setContactPrincipal(String contactPrincipal) {
        this.contactPrincipal = contactPrincipal;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public void setNumeroIdentificationFiscale(String numeroIdentificationFiscale) {
        this.numeroIdentificationFiscale = numeroIdentificationFiscale;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}