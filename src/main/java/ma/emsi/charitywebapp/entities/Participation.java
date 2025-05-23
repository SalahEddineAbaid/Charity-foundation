package ma.emsi.charitywebapp.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "participations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Participation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dateParticipation = LocalDateTime.now();
    private String statut;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Utilisateur utilisateur;

    @ManyToOne
    @JoinColumn(name = "action_id")
    private ActionCharite action;

    public Long getId() {
        return id;
    }

    public LocalDateTime getDateParticipation() {
        return dateParticipation;
    }

    public String getStatut() {
        return statut;
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

    public void setDateParticipation(LocalDateTime dateParticipation) {
        this.dateParticipation = dateParticipation;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public void setAction(ActionCharite action) {
        this.action = action;
    }
}