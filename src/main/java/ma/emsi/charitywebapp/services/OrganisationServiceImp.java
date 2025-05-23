package ma.emsi.charitywebapp.services;

import ma.emsi.charitywebapp.entities.Organisation;
import ma.emsi.charitywebapp.entities.Utilisateur;
import ma.emsi.charitywebapp.repositories.OrganisationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrganisationServiceImp implements OrganisationService {

    private final OrganisationRepository organisationRepository;

    @Autowired
    public OrganisationServiceImp(OrganisationRepository organisationRepository) {
        this.organisationRepository = organisationRepository;
    }

    @Override
    public Organisation saveOrganisation(Organisation organisation) {
        return organisationRepository.save(organisation);
    }

    @Override
    public Organisation updateOrganisation(Organisation organisation) {
        return organisationRepository.save(organisation);
    }

    @Override
    public Optional<Organisation> getOrganisationById(Long id) {
        return organisationRepository.findById(id);
    }

    @Override
    public List<Organisation> getAllOrganisations() {
        return organisationRepository.findAll();
    }

    @Override
    public List<Organisation> getOrganisationsByValidated(boolean validated) {
        return organisationRepository.findByValidated(validated);
    }

    @Override
    public List<Organisation> getOrganisationsByAdmin(Utilisateur admin) {
        return organisationRepository.findByAdmin(admin);
    }

    @Override
    public void deleteOrganisation(Long id) {
        organisationRepository.deleteById(id);
    }

    @Override
    public Organisation validateOrganisation(Long id) {
        Organisation organisation = organisationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organisation non trouvée"));
        organisation.setValidated(true);
        return organisationRepository.save(organisation);
    }

    @Override
    public List<Organisation> findAll() {
        return organisationRepository.findAll();
    }

    @Override
    public void valider(Long id, boolean validated) {
        Organisation org = organisationRepository.findById(id).orElseThrow();
        org.setValidated(validated);
        organisationRepository.save(org);
    }

    @Override
    public void toggleEnabled(Long id) {
        Organisation org = organisationRepository.findById(id).orElseThrow();
        org.setEnabled(!org.isEnabled());
        organisationRepository.save(org);
    }

    @Override
    public long countAll() {
        return organisationRepository.count();
    }

    @Override
    public List<Organisation> findByValidated(boolean validated) {
        return organisationRepository.findByValidated(validated);
    }

    @Override
    public long countPending() {
        return organisationRepository.countByValidated(false);
    }

    @Override
    public long countByValidated(boolean validated) {
        return organisationRepository.countByValidated(validated);
    }
}