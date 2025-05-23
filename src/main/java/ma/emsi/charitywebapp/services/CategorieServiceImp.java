package ma.emsi.charitywebapp.services;

import ma.emsi.charitywebapp.entities.Categorie;
import ma.emsi.charitywebapp.repositories.CategorieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategorieServiceImp implements CategorieService {

    private final CategorieRepository categorieRepository;

    @Autowired
    public CategorieServiceImp(CategorieRepository categorieRepository) {
        this.categorieRepository = categorieRepository;
    }

    @Override
    public Categorie saveCategorie(Categorie categorie) {
        return categorieRepository.save(categorie);
    }

    @Override
    public Categorie updateCategorie(Categorie categorie) {
        return categorieRepository.save(categorie);
    }

    @Override
    public Optional<Categorie> getCategorieById(Long id) {
        return categorieRepository.findById(id);
    }

    @Override
    public Optional<Categorie> getCategorieByNom(String nom) {
        return categorieRepository.findByNom(nom);
    }

    @Override
    public List<Categorie> getAllCategories() {
        return categorieRepository.findAll();
    }

    @Override
    public void deleteCategorie(Long id) {
        categorieRepository.deleteById(id);
    }
}