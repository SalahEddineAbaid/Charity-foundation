package ma.emsi.charitywebapp.controllers;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ma.emsi.charitywebapp.entities.Utilisateur;
import ma.emsi.charitywebapp.services.TwoFactorAuthService;
import ma.emsi.charitywebapp.services.UtilisateurService;
import ma.emsi.charitywebapp.services.TwoFactorAuthService.TwoFactorSetup;

@RestController
@RequestMapping("/api")
public class TwoFactorAuthController {

    @Autowired
    private TwoFactorAuthService twoFactorAuthService;
    
    @Autowired
    private UtilisateurService utilisateurService;
    
    @GetMapping("/generate-2fa-qr")
    public ResponseEntity<?> generateQrCode(Principal principal) {
        try {
            Utilisateur user = utilisateurService.getUserByEmail(principal.getName()).orElseThrow();
            TwoFactorSetup setup = twoFactorAuthService.generateNewSecret(user);
            
            Map<String, String> response = new HashMap<>();
            response.put("qrCodeUrl", "data:image/png;base64," + setup.getQrCodeImage());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/verify-2fa")
    public ResponseEntity<?> verifyAndEnable(Principal principal, @RequestBody Map<String, String> request) {
        try {
            String code = request.get("code");
            Utilisateur user = utilisateurService.getUserByEmail(principal.getName()).orElseThrow();
            
            boolean isValid = twoFactorAuthService.verifyCode(user.getTwoFactorSecret(), code);
            if (isValid) {
                user.setTwoFactorEnabled(true);
                utilisateurService.updateUser(user);
                return ResponseEntity.ok(Map.of("success", true));
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "Code invalide"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/disable-2fa")
    public ResponseEntity<?> disable2FA(Principal principal) {
        try {
            Utilisateur user = utilisateurService.getUserByEmail(principal.getName()).orElseThrow();
            user.setTwoFactorEnabled(false);
            user.setTwoFactorSecret(null);
            utilisateurService.updateUser(user);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
} 