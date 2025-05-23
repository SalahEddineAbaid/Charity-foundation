package ma.emsi.charitywebapp.services;

import ma.emsi.charitywebapp.entities.Utilisateur;
import ma.emsi.charitywebapp.repositories.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UtilisateurServiceImp implements UtilisateurService {

    private final UtilisateurRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UtilisateurServiceImp(UtilisateurRepository userRepository, @Lazy PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Utilisateur saveUser(Utilisateur user) {
        // Ensure roles list is initialized
        if (user.getRoles() == null) {
            user.setRoles(new ArrayList<>());
        }
        
        // Add role from Role entity to roles list if not already present
        if (user.getRole() != null) {
            String roleName = "ROLE_" + user.getRole().getNom();
            if (!user.getRoles().contains(roleName)) {
                user.getRoles().add(roleName);
            }
        }
        
        user.setMotDePasse(passwordEncoder.encode(user.getMotDePasse()));
        return userRepository.save(user);
    }

    @Override
    public Utilisateur updateUser(Utilisateur user) {
        // Ensure roles list is initialized
        if (user.getRoles() == null) {
            user.setRoles(new ArrayList<>());
        }
        
        // Add role from Role entity to roles list if not already present
        if (user.getRole() != null) {
            String roleName = "ROLE_" + user.getRole().getNom();
            if (!user.getRoles().contains(roleName)) {
                user.getRoles().add(roleName);
            }
        }
        
        if (user.getMotDePasse() != null && !user.getMotDePasse().isEmpty()) {
            user.setMotDePasse(passwordEncoder.encode(user.getMotDePasse()));
        } else {
            Utilisateur existingUser = userRepository.findById(user.getId()).orElse(null);
            if (existingUser != null) {
                user.setMotDePasse(existingUser.getMotDePasse());
            }
        }
        return userRepository.save(user);
    }

    @Override
    public Optional<Utilisateur> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<Utilisateur> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<Utilisateur> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public boolean checkEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("Tentative de connexion avec email: " + email);
        Utilisateur user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    System.out.println("Utilisateur non trouvé avec l'email: " + email);
                    return new UsernameNotFoundException("Utilisateur non trouvé avec l'email: " + email);
                });

        System.out.println("Utilisateur trouvé: " + user.getEmail());
        System.out.println("Rôles: " + user.getRoles());
        
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getMotDePasse(),
                user.isEnabled(),
                true,
                true,
                true,
                user.getRoles().stream()
                        .map(role -> {
                            System.out.println("Ajout du rôle: " + role);
                            return new SimpleGrantedAuthority(role);
                        })
                        .collect(Collectors.toList()));
    }

    @Override
    public List<Utilisateur> findAll() {
        return userRepository.findAll();
    }

    @Override
    public void toggleEnabled(Long id) {
        Utilisateur user = userRepository.findById(id).orElseThrow();
        user.setEnabled(!user.isEnabled());
        userRepository.save(user);
    }

    @Override
    public void updateRoles(Long id, List<String> roles) {
        Utilisateur user = userRepository.findById(id).orElseThrow();
        user.setRoles(roles);
        userRepository.save(user);
    }

    @Override
    public long countAll() {
        return userRepository.count();
    }

    @Override
    public List<Integer> getUsersCountByRole() {
        int admin = 0, org = 0, user = 0;
        for (Utilisateur u : userRepository.findAll()) {
            System.out.println("User: " + u.getEmail() + " Roles: " + u.getRoles());
            if (u.getRoles() != null) {
                if (u.getRoles().contains("ROLE_ADMIN"))
                    admin++;
                if (u.getRoles().contains("ROLE_ORGANISATION"))
                    org++;
                if (u.getRoles().contains("ROLE_UTILISATEUR"))
                    user++;
            }
        }
        return List.of(admin, org, user);
    }

    @Override
    public long countDonors() {
        return userRepository.countByRoleAndHasMadeDonation("USER", true);
    }

    @Override
    public long countByRole(String role) {
        return userRepository.countByRole(role);
    }

    @Override
    public List<Utilisateur> findByRole(String role) {
        try {
            return userRepository.findAllByRole(role);
        } catch (Exception e) {
            System.err.println("Erreur lors de la recherche des utilisateurs par rôle (" + role + "): " + e.getMessage());
            e.printStackTrace();
            // Fallback à l'implémentation précédente
            return userRepository.findAll().stream()
                .filter(u -> u.getRoles() != null && u.getRoles().contains(role))
                .collect(Collectors.toList());
        }
    }

    @Override
    public void setEmailVerified(Long id, boolean verified) {
        Utilisateur user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec l'ID: " + id));
        user.setEmailVerified(verified);
        userRepository.save(user);
    }
}