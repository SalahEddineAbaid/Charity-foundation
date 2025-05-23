package ma.emsi.charitywebapp.controllers;

import ma.emsi.charitywebapp.services.ActionChariteService;
import ma.emsi.charitywebapp.services.DonService;
import ma.emsi.charitywebapp.services.OrganisationService;
import ma.emsi.charitywebapp.services.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private ActionChariteService actionChariteService;
    
    @Autowired
    private OrganisationService organisationService;
    
    @Autowired
    private UtilisateurService utilisateurService;
    
    @Autowired
    private DonService donService;

    @GetMapping("/")
    public String index(Model model, HttpServletRequest request) {
        // Add current path for language switcher
        model.addAttribute("currentPath", request.getRequestURI());
        
        // Add dynamic statistics
        model.addAttribute("campaignsCount", actionChariteService.countAll());
        model.addAttribute("websitesCount", organisationService.countByValidated(true));
        
        // For countries, we'll use a mock value since actual country data might not be available
        List<String> countries = Arrays.asList("France", "Morocco", "Spain", "USA", "Canada", "UK", "Germany", "Italy", "Japan", "Australia");
        model.addAttribute("countriesCount", countries.size());
        
        // Add user stats
        model.addAttribute("donorsCount", utilisateurService.countDonors());
        model.addAttribute("volunteersCount", utilisateurService.countByRole("VOLUNTEER"));
        
        // Total amount collected
        model.addAttribute("totalDonations", donService.getTotalDonationAmount());
        
        return "index";
    }
    
    /**
     * Redirect /campaigns to the public actions list
     */
    @GetMapping("/campaigns")
    public String redirectToCampaigns() {
        return "redirect:/organisation/actions/public/list";
    }
    
    /**
     * Redirect /volunteer to the participations page
     */
    @GetMapping("/volunteer")
    public String redirectToVolunteer() {
        return "redirect:/participations";
    }
    
    /**
     * Redirect /campaigns/new to the action creation page
     */
    @GetMapping("/campaigns/new")
    public String redirectToNewCampaign() {
        return "redirect:/organisation/actions/create";
    }
    
    /**
     * Redirect /donate to the donation page
     */
    @GetMapping("/donate")
    public String redirectToDonate() {
        return "redirect:/organisation/actions/public/list?donate=true";
    }
}