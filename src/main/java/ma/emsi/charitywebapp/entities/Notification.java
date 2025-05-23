package ma.emsi.charitywebapp.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titre;

    @Column(columnDefinition = "TEXT")
    private String contenu;

    private boolean lu = false;
    private LocalDateTime dateCreation = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Utilisateur utilisateur;

    @ManyToOne
    @JoinColumn(name = "action_id")
    private ActionCharite action;

    public Long getId() {
        return id;
    }
    public String getTitre() {
        return titre;
    }

    public String getContenu() {
        return contenu;
    }

    public boolean isLu() {
        return lu;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public ActionCharite getAction() {
        return action;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public void setLu(boolean lu) {
        this.lu = lu;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public void setAction(ActionCharite action) {
        this.action = action;
    }
}