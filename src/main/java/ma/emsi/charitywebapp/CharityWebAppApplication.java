package ma.emsi.charitywebapp;

import ma.emsi.charitywebapp.entities.Role;
import ma.emsi.charitywebapp.entities.Utilisateur;
import ma.emsi.charitywebapp.repositories.RoleRepository;
import ma.emsi.charitywebapp.services.UtilisateurService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class CharityWebAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(CharityWebAppApplication.class, args);
    }

    @Bean
    CommandLineRunner initRoles(RoleRepository roleRepository) {
        return args -> {
            if (roleRepository.findByNom("UTILISATEUR").isEmpty()) {
                Role userRole = new Role();
                userRole.setNom("UTILISATEUR");
                roleRepository.save(userRole);
            }
            if (roleRepository.findByNom("ADMIN").isEmpty()) {
                Role adminRole = new Role();
                adminRole.setNom("ADMIN");
                roleRepository.save(adminRole);
            }
            if (roleRepository.findByNom("ORGANISATION").isEmpty()) {
                Role orgRole = new Role();
                orgRole.setNom("ORGANISATION");
                roleRepository.save(orgRole);
            }
        };
    }

    @Bean
    CommandLineRunner initAdmin(UtilisateurService utilisateurService, RoleRepository roleRepository,
            PasswordEncoder encoder) {
        return args -> {
            if (utilisateurService.getUserByEmail("admin@charityweb.com").isEmpty()) {
                Role adminRole = roleRepository.findByNom("ADMIN").orElseThrow();
                Utilisateur admin = new Utilisateur();
                admin.setNom("Admin");
                admin.setPrenom("Principal");
                admin.setEmail("admin@charityweb.com");
                admin.setMotDePasse("admin123");
                admin.setRole(adminRole);
                admin.setEnabled(true);
                admin.getRoles().add("ROLE_ADMIN");
                utilisateurService.saveUser(admin);
            }
        };
    }
}
