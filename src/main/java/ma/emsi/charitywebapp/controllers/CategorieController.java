package ma.emsi.charitywebapp.controllers;

import ma.emsi.charitywebapp.entities.Categorie;
import ma.emsi.charitywebapp.services.CategorieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/categories")
public class CategorieController {

    private final CategorieService categorieService;

    @Autowired
    public CategorieController(CategorieService categorieService) {
        this.categorieService = categorieService;
    }

    @PostMapping
    public ResponseEntity<Categorie> createCategorie(@RequestBody Categorie categorie) {
        Categorie savedCategorie = categorieService.saveCategorie(categorie);
        return new ResponseEntity<>(savedCategorie, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Categorie>> getAllCategories() {
        List<Categorie> categories = categorieService.getAllCategories();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Categorie> getCategorieById(@PathVariable Long id) {
        Optional<Categorie> categorie = categorieService.getCategorieById(id);
        return categorie.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/nom/{nom}")
    public ResponseEntity<Categorie> getCategorieByNom(@PathVariable String nom) {
        Optional<Categorie> categorie = categorieService.getCategorieByNom(nom);
        return categorie.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Categorie> updateCategorie(@PathVariable Long id, @RequestBody Categorie categorie) {
        Optional<Categorie> existingCategorie = categorieService.getCategorieById(id);
        if (existingCategorie.isPresent()) {
            categorie.setId(id);
            Categorie updatedCategorie = categorieService.updateCategorie(categorie);
            return new ResponseEntity<>(updatedCategorie, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategorie(@PathVariable Long id) {
        Optional<Categorie> existingCategorie = categorieService.getCategorieById(id);
        if (existingCategorie.isPresent()) {
            categorieService.deleteCategorie(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
