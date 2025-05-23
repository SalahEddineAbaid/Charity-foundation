package ma.emsi.charitywebapp.controllers;

import ma.emsi.charitywebapp.DTOs.UtilisateurDTO;
import ma.emsi.charitywebapp.entities.Role;
import ma.emsi.charitywebapp.entities.Utilisateur;
import ma.emsi.charitywebapp.services.RoleService;
import ma.emsi.charitywebapp.services.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UtilisateurController {

    private final UtilisateurService userService;
    private final RoleService roleService;

    @Autowired
    public UtilisateurController(UtilisateurService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @PostMapping(value = "/register", produces = "application/json")
    public ResponseEntity<?> registerUser(@RequestBody UtilisateurDTO userDTO) {
        if (userService.checkEmailExists(userDTO.getEmail())) {
            return ResponseEntity.badRequest().body("L'email existe déjà");
        }

        Role userRole = roleService.getRoleByNom("UTILISATEUR")
                .orElseThrow(() -> new RuntimeException("Rôle UTILISATEUR non trouvé"));

        Utilisateur user = new Utilisateur();
        user.setNom(userDTO.getNom());
        user.setPrenom(userDTO.getPrenom());
        user.setEmail(userDTO.getEmail());
        user.setMotDePasse(userDTO.getMotDePasse());
        user.setTelephone(userDTO.getTelephone());
        user.setAdresse(userDTO.getAdresse());
        user.setRole(userRole);
        user.setEnabled(true);
        user.getRoles().add("ROLE_UTILISATEUR");

        Utilisateur savedUser = userService.saveUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(savedUser));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        Optional<Utilisateur> user = userService.getUserById(id);
        return user.map(value -> ResponseEntity.ok(convertToDTO(value)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UtilisateurDTO>> getAllUsers() {
        List<Utilisateur> users = userService.getAllUsers();
        List<UtilisateurDTO> userDTOs = users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userDTOs);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UtilisateurDTO userDTO) {
        Optional<Utilisateur> optionalUser = userService.getUserById(id);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Utilisateur user = optionalUser.get();
        user.setNom(userDTO.getNom());
        user.setPrenom(userDTO.getPrenom());
        user.setTelephone(userDTO.getTelephone());
        user.setAdresse(userDTO.getAdresse());

        if (userDTO.getMotDePasse() != null && !userDTO.getMotDePasse().isEmpty()) {
            user.setMotDePasse(userDTO.getMotDePasse());
        }

        Utilisateur updatedUser = userService.updateUser(user);
        return ResponseEntity.ok(convertToDTO(updatedUser));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        Optional<Utilisateur> user = userService.getUserById(id);
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    private UtilisateurDTO convertToDTO(Utilisateur user) {
        UtilisateurDTO userDTO = new UtilisateurDTO();
        userDTO.setId(user.getId());
        userDTO.setNom(user.getNom());
        userDTO.setPrenom(user.getPrenom());
        userDTO.setEmail(user.getEmail());
        userDTO.setTelephone(user.getTelephone());
        userDTO.setAdresse(user.getAdresse());
        userDTO.setEnabled(user.isEnabled());
        userDTO.setRole(user.getRole() != null ? user.getRole().getNom() : null);
        return userDTO;
    }
}