package ma.emsi.charitywebapp.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "actions_charite")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActionCharite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String titre;

    @Column(columnDefinition = "TEXT")
    @NotBlank
    private String description;

    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private String lieu;

    @NotNull
    @Column(name = "objectif_collecte")
    private Double objectifCollecte;

    private double montantCollecte = 0;
    private String statut;
    private LocalDateTime dateCreation = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "categorie_id")
    private Categorie categorie;

    @ManyToOne
    @JoinColumn(name = "organisation_id")
    private Organisation organisation;

    @OneToMany(mappedBy = "action", cascade = CascadeType.ALL)
    private Set<Media> medias = new HashSet<>();

    @OneToMany(mappedBy = "action")
    private Set<Don> dons = new HashSet<>();

    @OneToMany(mappedBy = "action")
    private Set<Participation> participations = new HashSet<>();

    @OneToMany(mappedBy = "action")
    private Set<Notification> notifications = new HashSet<>();

    private String imageUrl;
    private String videoUrl;

    public void setId(Long id) {
        this.id = id;
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

    public void setObjectifCollecte(Double objectifCollecte) {
        this.objectifCollecte = objectifCollecte;
    }

    public void setMontantCollecte(double montantCollecte) {
        this.montantCollecte = montantCollecte;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public void setCategorie(Categorie categorie) {
        this.categorie = categorie;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }

    public void setMedias(Set<Media> medias) {
        this.medias = medias;
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

    public Long getId() {
        return id;
    }

    public String getTitre() {
        return titre;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
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

    public Double getObjectifCollecte() {
        return objectifCollecte;
    }

    public double getMontantCollecte() {
        return montantCollecte;
    }

    public String getStatut() {
        return statut;
    }

    public Categorie getCategorie() {
        return categorie;
    }

    public Organisation getOrganisation() {
        return organisation;
    }

    public Set<Media> getMedias() {
        return medias;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
}