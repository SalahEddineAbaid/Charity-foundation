package ma.emsi.charitywebapp.controllers;

import ma.emsi.charitywebapp.services.EmailVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/verify-email")
public class EmailVerificationController {

    @Autowired
    private EmailVerificationService verificationService;
    
    @GetMapping
    public String verifyEmail(@RequestParam String token, Model model) {
        boolean success = verificationService.verifyEmail(token);
        
        if (success) {
            model.addAttribute("success", "Votre email a été vérifié avec succès!");
        } else {
            model.addAttribute("error", "Le lien de vérification est invalide ou a expiré.");
        }
        
        return "verification-result";
    }
}
