package ma.emsi.charitywebapp.services;

import ma.emsi.charitywebapp.entities.Utilisateur;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;

public interface UtilisateurService extends UserDetailsService {
    Utilisateur saveUser(Utilisateur user);

    Utilisateur updateUser(Utilisateur user);

    Optional<Utilisateur> getUserById(Long id);

    Optional<Utilisateur> getUserByEmail(String email);

    List<Utilisateur> getAllUsers();

    void deleteUser(Long id);

    boolean checkEmailExists(String email);

    List<Utilisateur> findAll();

    void toggleEnabled(Long id);

    void updateRoles(Long id, List<String> roles);

    long countAll();

    List<Integer> getUsersCountByRole();

    long countDonors();

    long countByRole(String role);

    List<Utilisateur> findByRole(String role);

    void setEmailVerified(Long id, boolean verified);
}