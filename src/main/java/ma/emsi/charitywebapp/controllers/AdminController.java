package ma.emsi.charitywebapp.controllers;

import ma.emsi.charitywebapp.entities.Organisation;
import ma.emsi.charitywebapp.entities.Utilisateur;
import ma.emsi.charitywebapp.entities.ActionCharite;
import ma.emsi.charitywebapp.entities.Don;
import ma.emsi.charitywebapp.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Arrays;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private OrganisationService organisationService;
    @Autowired
    private UtilisateurService utilisateurService;
    @Autowired
    private ActionChariteService actionChariteService;
    @Autowired
    private DonService donService;

    // Dashboard
    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpServletRequest request) {
        model.addAttribute("currentUri", request.getRequestURI());
        model.addAttribute("totalActions", actionChariteService.countAll());
        model.addAttribute("totalOrganisations", organisationService.countByValidated(true));
        model.addAttribute("totalDons", donService.countAll());
        model.addAttribute("totalMontant", donService.sumMontant());
        model.addAttribute("totalUtilisateurs", utilisateurService.countByRole("ROLE_UTILISATEUR"));
        model.addAttribute("donsParMois", donService.getDonsParMois());
        model.addAttribute("orgsEnAttente", organisationService.countPending());
        model.addAttribute("actionsActives", actionChariteService.countActive());
        model.addAttribute("montantTotalDons", donService.sumMontant());
        model.addAttribute("actionsParMois", actionChariteService.getActionsParMois());
        long adminCount = utilisateurService.countByRole("ROLE_ADMIN");
        long orgCount = utilisateurService.countByRole("ROLE_ORGANISATION");
        long userCount = utilisateurService.countByRole("ROLE_UTILISATEUR");
        model.addAttribute("rolesLabels", List.of("ADMIN", "ORGANISATION", "UTILISATEUR"));
        model.addAttribute("rolesData", Arrays.asList(adminCount, orgCount, userCount));

        // Pour le débogage - afficher tous les utilisateurs et leurs rôles
        List<Utilisateur> allUsers = utilisateurService.findAll();
        System.out.println("Nombre total d'utilisateurs: " + allUsers.size());
        allUsers.forEach(u -> {
            System.out.println("User: " + u.getEmail() + " | ID: " + u.getId() + " | Roles: " + 
                (u.getRoles() != null ? u.getRoles() : "null") + 
                " | Role Object: " + (u.getRole() != null ? u.getRole().getNom() : "null") +
                " | Enabled: " + u.isEnabled());
        });
        
        return "admin/dashboard";
    }

    // Organisations
    @GetMapping("/organisations")
    public String organisations(@RequestParam(value = "filter", required = false) String filter, Model model) {
        List<Organisation> organisations;
        if ("validées".equals(filter)) {
            organisations = organisationService.findByValidated(true);
        } else if ("nonvalidées".equals(filter)) {
            organisations = organisationService.findByValidated(false);
        } else {
            organisations = organisationService.findAll();
        }
        model.addAttribute("organisations", organisations);
        model.addAttribute("filter", filter);
        return "admin/organisations";
    }

    @PostMapping("/organisations/{id}/valider")
    public String validerOrganisation(@PathVariable Long id) {
        organisationService.valider(id, true);
        return "redirect:/admin/organisations?success";
    }

    @PostMapping("/organisations/{id}/refuser")
    public String refuserOrganisation(@PathVariable Long id) {
        organisationService.valider(id, false);
        return "redirect:/admin/organisations?refused";
    }

    @PostMapping("/organisations/{id}/toggle")
    public String toggleOrganisation(@PathVariable Long id) {
        organisationService.toggleEnabled(id);
        return "redirect:/admin/organisations";
    }

    // Utilisateurs
    @GetMapping("/utilisateurs")
    public String utilisateurs(Model model) {
        List<Utilisateur> utilisateursVisibles = utilisateurService.findAll().stream()
                .filter(u -> u.getRoles() != null
                        && u.getRoles().stream().noneMatch(r -> r.equals("ROLE_ADMIN") || r.equals("ADMIN")))
                .toList();
        model.addAttribute("utilisateurs", utilisateursVisibles);
        return "admin/utilisateurs";
    }

    @PostMapping("/utilisateurs/{id}/toggle")
    public String toggleUtilisateur(@PathVariable Long id) {
        utilisateurService.toggleEnabled(id);
        return "redirect:/admin/utilisateurs";
    }

    @PostMapping("/utilisateurs/{id}/roles")
    public String updateRoles(@PathVariable Long id, @RequestParam List<String> roles) {
        utilisateurService.updateRoles(id, roles);
        return "redirect:/admin/utilisateurs";
    }

    @GetMapping("/statistiques")
    public String statistiques(Model model, HttpServletRequest request) {
        model.addAttribute("currentUri", request.getRequestURI());
        
        // Nombre d'utilisateurs par rôle
        long adminCount = utilisateurService.countByRole("ROLE_ADMIN");
        long orgCount = utilisateurService.countByRole("ROLE_ORGANISATION");
        long userCount = utilisateurService.countByRole("ROLE_UTILISATEUR");
        model.addAttribute("adminCount", adminCount);
        model.addAttribute("orgCount", orgCount);
        model.addAttribute("userCount", userCount);
        model.addAttribute("totalUsers", adminCount + orgCount + userCount);
        
        // Données pour le graphique de répartition des utilisateurs
        model.addAttribute("rolesLabels", List.of("ADMIN", "ORGANISATION", "UTILISATEUR"));
        model.addAttribute("rolesData", Arrays.asList(adminCount, orgCount, userCount));
        
        // Nombre d'actions par statut
        long actionsActives = actionChariteService.countActive();
        long actionsTerminees = actionChariteService.getActionsByStatut("termine").size();
        long actionsArchivees = actionChariteService.getActionsByStatut("archive").size();
        model.addAttribute("actionsActives", actionsActives);
        model.addAttribute("actionsTerminees", actionsTerminees);
        model.addAttribute("actionsArchivees", actionsArchivees);
        model.addAttribute("totalActions", actionsActives + actionsTerminees + actionsArchivees);
        
        // Données pour le graphique de répartition des actions
        model.addAttribute("actionLabels", List.of("Actives", "Terminées", "Archivées"));
        model.addAttribute("actionData", Arrays.asList(actionsActives, actionsTerminees, actionsArchivees));
        
        // Statistiques des dons
        double montantTotal = donService.sumMontant();
        model.addAttribute("montantTotal", montantTotal);
        model.addAttribute("nombreDons", donService.countAll());
        
        // Dons par mois pour le graphique
        model.addAttribute("donsParMois", donService.getDonsParMois());
        
        // Actions créées par mois pour le graphique
        model.addAttribute("actionsParMois", actionChariteService.getActionsParMois());
        
        return "admin/statistiques";
    }

    @GetMapping("/parametres")
    public String parametres(Model model) {
        // Tu peux ajouter ici des données à passer à la vue si besoin
        return "admin/parametres"; // Le nom du template à afficher
    }

    @PostMapping("/parametres/password")
    public String changePassword(@RequestParam String oldPassword, @RequestParam String newPassword, Model model) {
        // Ajoute ici la logique de changement de mot de passe
        model.addAttribute("message", "Mot de passe changé !");
        return "admin/parametres";
    }

    @PostMapping("/parametres/notifications")
    public String updateNotifications(@RequestParam(required = false) String emailNotifications, Model model) {
        // Ajoute ici la logique de gestion des notifications
        model.addAttribute("message", "Préférences de notifications mises à jour !");
        return "admin/parametres";
    }
}
