package ma.emsi.charitywebapp.services;

import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import ma.emsi.charitywebapp.entities.Utilisateur;
import ma.emsi.charitywebapp.repositories.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class TwoFactorAuthService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;
    
    private final SecretGenerator secretGenerator = new DefaultSecretGenerator();
    private final QrGenerator qrGenerator = new ZxingPngQrGenerator();
    private final TimeProvider timeProvider = new SystemTimeProvider();
    private final CodeGenerator codeGenerator = new DefaultCodeGenerator();
    private final CodeVerifier verifier = new DefaultCodeVerifier(codeGenerator, timeProvider);
    
    public TwoFactorSetup generateNewSecret(Utilisateur user) {
        String secret = generateSecretKey();
        String qrCodeImage = generateQrCodeImage(user.getEmail(), secret);
        
        // Stocker le secret temporairement (non activé)
        user.setTwoFactorSecret(secret);
        user.setTwoFactorEnabled(false);
        utilisateurRepository.save(user);
        
        return new TwoFactorSetup(secret, qrCodeImage);
    }
    
    private String generateSecretKey() {
        return secretGenerator.generate();
    }
    
    private String generateQrCodeImage(String email, String secret) {
        String issuer = "CharityWebApp";
        
        try {
            QrData data = new QrData.Builder()
                    .label(email)
                    .secret(secret)
                    .issuer(issuer)
                    .algorithm(HashingAlgorithm.SHA1)
                    .digits(6)
                    .period(30)
                    .build();
            
            byte[] qrCodeBytes = qrGenerator.generate(data);
            return Base64.getEncoder().encodeToString(qrCodeBytes);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du QR code", e);
        }
    }
    
    public boolean verifyCode(String secret, String code) {
        return verifier.isValidCode(secret, code);
    }
    
    public boolean enableTwoFactor(Long userId, String code) {
        Utilisateur user = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));
        
        if (verifyCode(user.getTwoFactorSecret(), code)) {
            user.setTwoFactorEnabled(true);
            utilisateurRepository.save(user);
            return true;
        }
        return false;
    }
    
    public boolean validateLogin(Utilisateur user, String code) {
        if (!user.isTwoFactorEnabled()) {
            return true;
        }
        return verifyCode(user.getTwoFactorSecret(), code);
    }
    
    // Inner class to hold 2FA setup data
    public static class TwoFactorSetup {
        private String secret;
        private String qrCodeImage;
        
        public TwoFactorSetup(String secret, String qrCodeImage) {
            this.secret = secret;
            this.qrCodeImage = qrCodeImage;
        }
        
        public String getSecret() {
            return secret;
        }
        
        public String getQrCodeImage() {
            return qrCodeImage;
        }
    }
}
