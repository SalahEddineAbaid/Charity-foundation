package ma.emsi.charitywebapp.controllers;

import ma.emsi.charitywebapp.entities.ActionCharite;
import ma.emsi.charitywebapp.entities.Don;
import ma.emsi.charitywebapp.entities.Utilisateur;
import ma.emsi.charitywebapp.entities.UserActivity;
import ma.emsi.charitywebapp.services.ActionChariteService;
import ma.emsi.charitywebapp.services.ActivityService;
import ma.emsi.charitywebapp.services.DonService;
import ma.emsi.charitywebapp.services.UtilisateurService;
import ma.emsi.charitywebapp.services.CategorieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class UserWebController {

    @Autowired
    private UtilisateurService utilisateurService;

    @Autowired
    private DonService donService;

    @Autowired
    private ActionChariteService actionChariteService;

    @Autowired
    private CategorieService categorieService;
    
    @Autowired
    private ActivityService activityService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    // Add user to model for all methods in this controller
    @ModelAttribute
    public void addUserToModel(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            utilisateurService.getUserByEmail(authentication.getName())
                .ifPresent(user -> model.addAttribute("user", user));
        }
    }

    @GetMapping("/user/profile")
    public String userProfile(Model model, Authentication authentication) {
        Utilisateur user = utilisateurService.getUserByEmail(authentication.getName()).orElseThrow();
        
        // Get user activity history for the Activities tab
        List<UserActivity> activities = activityService.getUserActivities(user, 10);
        
        model.addAttribute("activities", activities);
        model.addAttribute("currentPath", "/user/profile");
        return "user/profile";
    }

    @PostMapping("/user/profile")
    public String updateProfile(@ModelAttribute Utilisateur user, Authentication authentication, Model model) {
        Utilisateur current = utilisateurService.getUserByEmail(authentication.getName()).orElseThrow();
        current.setNom(user.getNom());
        current.setPrenom(user.getPrenom());
        current.setTelephone(user.getTelephone());
        current.setAdresse(user.getAdresse());
        utilisateurService.updateUser(current);
        model.addAttribute("currentPath", "/user/profile");
        model.addAttribute("success", "Profil mis à jour !");
        return "user/profile";
    }
    
    @PostMapping("/user/change-password")
    public String changePassword(
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            Authentication authentication,
            RedirectAttributes redirectAttrs) {
        
        Utilisateur user = utilisateurService.getUserByEmail(authentication.getName()).orElseThrow();
        
        // Check if current password is correct
        if (!passwordEncoder.matches(currentPassword, user.getMotDePasse())) {
            redirectAttrs.addFlashAttribute("error", "Le mot de passe actuel est incorrect");
            return "redirect:/user/profile";
        }
        
        // Check if new passwords match
        if (!newPassword.equals(confirmPassword)) {
            redirectAttrs.addFlashAttribute("error", "Les nouveaux mots de passe ne correspondent pas");
            return "redirect:/user/profile";
        }
        
        // Check password strength
        if (newPassword.length() < 8 || 
            !newPassword.matches(".*[A-Z].*") || 
            !newPassword.matches(".*[a-z].*") || 
            !newPassword.matches(".*[0-9].*") || 
            !newPassword.matches(".*[!@#$%^&*()].*")) {
            
            redirectAttrs.addFlashAttribute("error", 
                "Le mot de passe doit contenir au moins 8 caractères, une majuscule, une minuscule, un chiffre et un caractère spécial");
            return "redirect:/user/profile";
        }
        
        // Update password
        user.setMotDePasse(passwordEncoder.encode(newPassword));
        utilisateurService.updateUser(user);
        
        // Log activity
        activityService.logActivity(user, "password_change", "Changement de mot de passe");
        
        redirectAttrs.addFlashAttribute("success", "Mot de passe mis à jour avec succès");
        return "redirect:/user/profile";
    }

    @GetMapping("/user/dons")
    public String userDons(
            Model model, 
            Authentication authentication,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Long actionId) {
        
        Utilisateur user = utilisateurService.getUserByEmail(authentication.getName()).orElseThrow();
        
        // Get all user donations
        List<Don> allDons = donService.getDonsByDonateur(user);
        
        // Apply filters if present
        List<Don> filteredDons = applyDonationFilters(allDons, startDate, endDate, actionId);
        
        // Calculate statistics
        double totalDonated = filteredDons.stream().mapToDouble(Don::getMontant).sum();
        int donCount = filteredDons.size();
        
        // Get unique actions that user has supported
        Set<ActionCharite> uniqueActions = allDons.stream()
                .map(Don::getAction)
                .collect(Collectors.toSet());
        int uniqueActionCount = uniqueActions.size();
        
        // Prepare chart data - last 12 months
        Map<String, Double> chartData = generateDonationChartData(allDons);
        
        // Add attributes to model
        model.addAttribute("dons", filteredDons);
        model.addAttribute("user", user);
        model.addAttribute("totalDonated", totalDonated);
        model.addAttribute("donCount", donCount);
        model.addAttribute("uniqueActionCount", uniqueActionCount);
        model.addAttribute("uniqueActions", uniqueActions);
        model.addAttribute("chartData", chartData);
        model.addAttribute("currentPath", "/user/dons");
        
        return "user/dons";
    }

    @GetMapping("/user/dons/recu/{id}")
    public ResponseEntity<byte[]> downloadReceipt(@PathVariable Long id, Authentication authentication) {
        Utilisateur user = utilisateurService.getUserByEmail(authentication.getName()).orElseThrow();
        Don don = donService.getDonById(id).orElseThrow(() -> new RuntimeException("Don non trouvé"));
        
        // Verify that the donation belongs to the authenticated user
        if (!don.getDonateur().getId().equals(user.getId())) {
            return ResponseEntity.badRequest().build();
        }
        
        // Here you would normally generate a PDF receipt
        // For now we'll just return a simple text receipt
        String receiptContent = generateDonationReceipt(don);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.setContentDispositionFormData("attachment", "recu_don_" + id + ".txt");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(receiptContent.getBytes());
    }

    @GetMapping("/user/actions")
    public String userActions(
            Model model, 
            Authentication authentication,
            @RequestParam(required = false) Long categorieId) {
            
        List<ActionCharite> actions = (categorieId != null)
                ? actionChariteService
                        .getActionsByCategorie(categorieService.getCategorieById(categorieId).orElse(null))
                : actionChariteService.getAllActionsCharite();
                
        model.addAttribute("actions", actions);
        model.addAttribute("categories", categorieService.getAllCategories());
        model.addAttribute("selectedCategorieId", categorieId);
        model.addAttribute("currentPath", "/user/actions");
        return "user/actions";
    }

    // Helper methods
    
    private List<Don> applyDonationFilters(List<Don> donations, String startDateStr, String endDateStr, Long actionId) {
        List<Don> result = new ArrayList<>(donations);
        
        // Filter by start date
        if (startDateStr != null && !startDateStr.isEmpty()) {
            LocalDateTime startDate = LocalDateTime.parse(startDateStr + "T00:00:00");
            result = result.stream()
                    .filter(don -> don.getDateDon() != null && don.getDateDon().isAfter(startDate))
                    .collect(Collectors.toList());
        }
        
        // Filter by end date
        if (endDateStr != null && !endDateStr.isEmpty()) {
            LocalDateTime endDate = LocalDateTime.parse(endDateStr + "T23:59:59");
            result = result.stream()
                    .filter(don -> don.getDateDon() != null && don.getDateDon().isBefore(endDate))
                    .collect(Collectors.toList());
        }
        
        // Filter by action
        if (actionId != null) {
            result = result.stream()
                    .filter(don -> don.getAction() != null && don.getAction().getId().equals(actionId))
                    .collect(Collectors.toList());
        }
        
        return result;
    }
    
    private Map<String, Double> generateDonationChartData(List<Don> donations) {
        // Create a map to store month -> total donations
        Map<YearMonth, Double> monthlyDonations = new TreeMap<>();
        
        // Get last 12 months
        YearMonth currentMonth = YearMonth.now();
        for (int i = 11; i >= 0; i--) {
            YearMonth month = currentMonth.minusMonths(i);
            monthlyDonations.put(month, 0.0);
        }
        
        // Sum donations by month
        for (Don don : donations) {
            if (don.getDateDon() != null) {
                YearMonth donMonth = YearMonth.from(don.getDateDon());
                if (monthlyDonations.containsKey(donMonth)) {
                    monthlyDonations.put(donMonth, monthlyDonations.get(donMonth) + don.getMontant());
                }
            }
        }
        
        // Format the map for the chart (month name -> amount)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy");
        Map<String, Double> chartData = new LinkedHashMap<>();
        
        for (Map.Entry<YearMonth, Double> entry : monthlyDonations.entrySet()) {
            chartData.put(entry.getKey().format(formatter), entry.getValue());
        }
        
        return chartData;
    }
    
    private String generateDonationReceipt(Don don) {
        StringBuilder receipt = new StringBuilder();
        receipt.append("REÇU FISCAL DE DON\n");
        receipt.append("===================\n\n");
        receipt.append("Numéro de don: ").append(don.getId()).append("\n");
        receipt.append("Date du don: ").append(don.getDateDon().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("\n");
        receipt.append("Donateur: ").append(don.getDonateur().getPrenom()).append(" ").append(don.getDonateur().getNom()).append("\n");
        receipt.append("Adresse: ").append(don.getDonateur().getAdresse()).append("\n\n");
        receipt.append("Montant: ").append(don.getMontant()).append(" €\n");
        receipt.append("Méthode de paiement: ").append(don.getMethodePaiement()).append("\n");
        receipt.append("Référence de paiement: ").append(don.getReferencePaiement()).append("\n\n");
        receipt.append("Action soutenue: ").append(don.getAction().getTitre()).append("\n");
        if (don.getAction().getOrganisation() != null) {
            receipt.append("Organisation: ").append(don.getAction().getOrganisation().getNom()).append("\n\n");
        }
        receipt.append("Merci pour votre générosité!\n");
        receipt.append("Ce reçu peut être utilisé pour votre déclaration fiscale.\n");
        return receipt.toString();
    }
}
