package ma.emsi.charitywebapp.services;

import ma.emsi.charitywebapp.entities.Categorie;

import java.util.List;
import java.util.Optional;

public interface CategorieService {
    Categorie saveCategorie(Categorie categorie);
    Categorie updateCategorie(Categorie categorie);
    Optional<Categorie> getCategorieById(Long id);
    Optional<Categorie> getCategorieByNom(String nom);
    List<Categorie> getAllCategories();
    void deleteCategorie(Long id);
}