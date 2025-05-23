package ma.emsi.charitywebapp.services;

import ma.emsi.charitywebapp.entities.ActionCharite;
import ma.emsi.charitywebapp.entities.Participation;
import ma.emsi.charitywebapp.entities.Utilisateur;

import java.util.List;
import java.util.Optional;

public interface ParticipationService {
    Participation saveParticipation(Participation participation);

    Participation updateParticipation(Participation participation);

    Optional<Participation> getParticipationById(Long id);

    List<Participation> getAllParticipations();

    List<Participation> getParticipationsByAction(ActionCharite action);

    List<Participation> getParticipationsByUtilisateur(Utilisateur utilisateur);

    Optional<Participation> getParticipationByUtilisateurAndAction(Utilisateur utilisateur, ActionCharite action);

    void deleteParticipation(Long id);

    long countByAction(ActionCharite action);

    void updateParticipationStatut(Long participationId, String statut);
    public int countParticipationsByOrganisationId(Long organisationId);
}