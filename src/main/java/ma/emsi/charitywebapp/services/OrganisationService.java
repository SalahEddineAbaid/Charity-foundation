package ma.emsi.charitywebapp.services;

import ma.emsi.charitywebapp.entities.Organisation;
import ma.emsi.charitywebapp.entities.Utilisateur;

import java.util.List;
import java.util.Optional;

public interface OrganisationService {
    Organisation saveOrganisation(Organisation organisation);

    Organisation updateOrganisation(Organisation organisation);

    Optional<Organisation> getOrganisationById(Long id);

    List<Organisation> getAllOrganisations();

    List<Organisation> getOrganisationsByValidated(boolean validated);

    List<Organisation> getOrganisationsByAdmin(Utilisateur admin);

    void deleteOrganisation(Long id);

    Organisation validateOrganisation(Long id);

    List<Organisation> findAll();

    List<Organisation> findByValidated(boolean validated);

    void valider(Long id, boolean validated);

    void toggleEnabled(Long id);

    long countAll();

    long countPending();

    long countByValidated(boolean validated);
}