package ma.emsi.charitywebapp.services;

import ma.emsi.charitywebapp.entities.Utilisateur;
import ma.emsi.charitywebapp.repositories.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;
import java.util.UUID;
import java.util.HashMap;
import java.util.Map;

@Service
public class EmailVerificationService {
    
    @Autowired
    private UtilisateurService utilisateurService;
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${app.base-url}")
    private String baseUrl;
    
    // Map temporaire pour stocker les tokens (à remplacer par une table en DB)
    private Map<String, Long> tokenStorage = new HashMap<>();
    
    public void sendVerificationEmail(Utilisateur user) {
        String token = generateVerificationToken(user);
        String verificationUrl = baseUrl + "/verify-email?token=" + token;
        
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Vérification de votre adresse email");
        message.setText("Bonjour " + user.getPrenom() + ",\n\n" +
                        "Veuillez confirmer votre adresse email en cliquant sur le lien suivant : \n" +
                        verificationUrl + "\n\n" +
                        "Ce lien expirera dans 24 heures.");
        
        mailSender.send(message);
    }
    
    private String generateVerificationToken(Utilisateur user) {
        // Générer un token unique avec une date d'expiration
        String tokenValue = UUID.randomUUID().toString();
        // Stocker le token en base de données avec l'ID utilisateur
        tokenStorage.put(tokenValue, user.getId());
        return tokenValue;
    }
    
    @Transactional
    public boolean verifyEmail(String token) {
        // Vérifier si le token existe et est valide
        if (tokenStorage.containsKey(token)) {
            Long userId = tokenStorage.get(token);
            // Mettre à jour le statut de vérification de l'utilisateur
            utilisateurService.setEmailVerified(userId, true);
            // Supprimer le token utilisé
            tokenStorage.remove(token);
            return true;
        }
        return false;
    }
}
