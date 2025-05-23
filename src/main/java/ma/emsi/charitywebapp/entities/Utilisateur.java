package ma.emsi.charitywebapp.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Utilisateur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String prenom;

    @Column(unique = true)
    private String email;

    private String motDePasse;
    private String telephone;
    private String adresse;
    private boolean enabled = true;
    private LocalDateTime dateCreation = LocalDateTime.now();
    
    @Column(name = "photo_url")
    private String photoUrl;
    
    @Column(name = "email_verified")
    private Boolean emailVerified = false;
    
    @Column(name = "two_factor_secret")
    private String twoFactorSecret;
    
    @Column(name = "two_factor_enabled")
    private Boolean twoFactorEnabled = false;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @OneToMany(mappedBy = "donateur")
    private Set<Don> dons = new HashSet<>();

    @OneToMany(mappedBy = "utilisateur")
    private Set<Participation> participations = new HashSet<>();

    @OneToMany(mappedBy = "utilisateur")
    private Set<Notification> notifications = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getEmail() {
        return email;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getAdresse() {
        return adresse;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public Role getRole() {
        return role;
    }

    public Set<Don> getDons() {
        return dons;
    }

    public Set<Participation> getParticipations() {
        return participations;
    }

    public Set<Notification> getNotifications() {
        return notifications;
    }

    public List<String> getRoles() {
        return roles;
    }

    public String getTwoFactorSecret() {
        return twoFactorSecret;
    }

    public void setTwoFactorSecret(String twoFactorSecret) {
        this.twoFactorSecret = twoFactorSecret;
    }

    public Boolean isTwoFactorEnabled() {
        return twoFactorEnabled != null ? twoFactorEnabled : false;
    }

    public void setTwoFactorEnabled(Boolean twoFactorEnabled) {
        this.twoFactorEnabled = twoFactorEnabled;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setDons(Set<Don> dons) {
        this.dons = dons;
    }

    public void setParticipations(Set<Participation> participations) {
        this.participations = participations;
    }

    public void setNotifications(Set<Notification> notifications) {
        this.notifications = notifications;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public Boolean isEmailVerified() {
        return emailVerified != null ? emailVerified : false;
    }

    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}