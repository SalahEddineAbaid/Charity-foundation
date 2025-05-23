package ma.emsi.charitywebapp.controllers;

import jakarta.validation.Valid;
import ma.emsi.charitywebapp.entities.ActionCharite;
import ma.emsi.charitywebapp.entities.Organisation;
import ma.emsi.charitywebapp.entities.Utilisateur;
import ma.emsi.charitywebapp.entities.Participation;
import ma.emsi.charitywebapp.entities.Don;
import ma.emsi.charitywebapp.entities.Notification;
import ma.emsi.charitywebapp.services.ActionChariteService;
import ma.emsi.charitywebapp.services.OrganisationService;
import ma.emsi.charitywebapp.services.ParticipationService;
import ma.emsi.charitywebapp.services.UtilisateurService;
import ma.emsi.charitywebapp.services.ImageStorageService;
import ma.emsi.charitywebapp.services.DonService;
import ma.emsi.charitywebapp.services.NotificationService;
import ma.emsi.charitywebapp.services.CategorieService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.security.Principal;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/organisation")
public class OrganisationWebController {

    private static final Logger logger = LoggerFactory.getLogger(OrganisationWebController.class);

    @Autowired
    private ActionChariteService actionService;
    @Autowired
    private OrganisationService organisationService;
    @Autowired
    private UtilisateurService utilisateurService;
    @Autowired
    private ParticipationService participationService;
    @Autowired
    private ImageStorageService imageStorageService;
    @Autowired
    private DonService donService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private CategorieService categorieService;

    @GetMapping("/actions/create")
    public String showCreateForm(Model model, Authentication authentication) {
        model.addAttribute("action", new ActionCharite());
        Utilisateur user = utilisateurService.getUserByEmail(authentication.getName()).orElseThrow();
        Organisation org = organisationService.getOrganisationsByAdmin(user).get(0);
        model.addAttribute("organisation", org);
        return "organisation/action-form";
    }

    @PostMapping("/actions/create")
    public String createAction(
            @Valid @ModelAttribute("action") ActionCharite action,
            BindingResult result,
            @RequestParam("imageFile") MultipartFile imageFile,
            Authentication authentication,
            Model model) {

        Utilisateur user = utilisateurService.getUserByEmail(authentication.getName()).orElseThrow();
        Organisation org = organisationService.getOrganisationsByAdmin(user).get(0);

        // Toujours ajouter organisation au modèle pour le sidebar
        model.addAttribute("organisation", org);

        if (result.hasErrors()) {
            // Toujours remettre l'action dans le modèle pour garder les valeurs et afficher
            // les erreurs
            model.addAttribute("action", action);
            return "organisation/action-form";
        }

        action.setOrganisation(org);
        // Définir un statut par défaut si non spécifié
        if (action.getStatut() == null || action.getStatut().isEmpty()) {
            action.setStatut("active");
        }

        // Gestion de l'image (optionnelle)
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = actionService.saveImage(imageFile);
            if (imageUrl != null)
                action.setImageUrl(imageUrl);
        }

        actionService.saveActionCharite(action);

        // Redirection vers la liste des actions si tout est OK
        return "redirect:/organisation/actions";
    }

    @GetMapping("/actions/edit/{id}")
    public String editAction(@PathVariable Long id, Model model, Authentication authentication) {
        ActionCharite action = actionService.getActionChariteById(id).orElseThrow();
        model.addAttribute("action", action);
        Utilisateur user = utilisateurService.getUserByEmail(authentication.getName()).orElseThrow();
        Organisation org = organisationService.getOrganisationsByAdmin(user).get(0);
        model.addAttribute("organisation", org);
        return "organisation/action-form";
    }

    @PostMapping("/actions/edit/{id}")
    public String updateAction(@PathVariable Long id, @Valid @ModelAttribute("action") ActionCharite action,
            BindingResult result,
            @RequestParam("imageFile") MultipartFile imageFile,
            Authentication authentication) {
        if (result.hasErrors())
            return "organisation/action-form";
        Utilisateur user = utilisateurService.getUserByEmail(authentication.getName()).orElseThrow();
        Organisation org = organisationService.getOrganisationsByAdmin(user).get(0);
        action.setOrganisation(org);
        action.setId(id);
        String imageUrl = actionService.saveImage(imageFile);
        if (imageUrl != null)
            action.setImageUrl(imageUrl);
        actionService.updateActionCharite(action);
        return "redirect:/organisation/actions";
    }

    @GetMapping("/actions")
    public String listActions(@RequestParam(required = false) String statut, 
                              Model model, 
                              Authentication authentication,
                              HttpServletRequest request) {
        logger.debug("Début de la méthode listActions, statut={}", statut);
        
        Utilisateur user = utilisateurService.getUserByEmail(authentication.getName()).orElseThrow();
        logger.debug("Utilisateur trouvé: id={}, email={}", user.getId(), user.getEmail());
        
        Organisation org = organisationService.getOrganisationsByAdmin(user).get(0);
        logger.debug("Organisation trouvée: id={}, nom={}", org.getId(), org.getNom());
        
        // Utiliser les nouvelles méthodes du service
        List<ActionCharite> actions;
        if (statut != null && !statut.isEmpty()) {
            // Si on ne peut pas utiliser findByOrganisationIdDirect avec statut, filtrer après
            actions = actionService.findByOrganisationIdDirect(org.getId());
            actions = actions.stream()
                    .filter(a -> statut.equals(a.getStatut()))
                    .toList();
            logger.debug("Recherche d'actions par ID d'organisation et filtrage par statut: {}, trouvées: {}", statut, actions.size());
        } else {
            actions = actionService.findByOrganisationIdDirect(org.getId());
            logger.debug("Recherche de toutes les actions par ID d'organisation, trouvées: {}", actions.size());
        }
        
        // Vérification des actions récupérées
        if (actions.isEmpty()) {
            logger.warn("Aucune action trouvée pour l'organisation id={}, nom={}", org.getId(), org.getNom());
        } else {
            logger.debug("Actions trouvées pour l'organisation: {}", actions.stream().map(a -> a.getId() + ":" + a.getTitre()).toList());
        }
        
        model.addAttribute("actions", actions);
        model.addAttribute("organisation", org);
        model.addAttribute("statut", statut);
        model.addAttribute("currentPath", request.getRequestURI());
        return "organisation/actions";
    }

    @PostMapping("/actions/archive/{id}")
    public String archiveAction(@PathVariable Long id) {
        actionService.archiverAction(id);
        return "redirect:/organisation/actions";
    }

    @PostMapping("/actions/delete/{id}")
    public String deleteAction(@PathVariable Long id) {
        actionService.deleteActionCharite(id);
        return "redirect:/organisation/actions";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        Utilisateur user = utilisateurService.getUserByEmail(authentication.getName()).orElseThrow();
        Organisation org = organisationService.getOrganisationsByAdmin(user).get(0);
        List<ActionCharite> actions = actionService.getActionsByOrganisation(org);

        int totalActions = actions.size();
        
        // Éviter d'accéder aux collections lazy de participations
        // Utilisez une requête directe pour compter les participations
        int totalParticipants = participationService.countParticipationsByOrganisationId(org.getId());
        
        double totalCollecte = actions.stream()
                .mapToDouble(ActionCharite::getMontantCollecte)
                .sum();

        model.addAttribute("organisation", org);
        model.addAttribute("totalActions", totalActions);
        model.addAttribute("totalParticipants", totalParticipants);
        model.addAttribute("totalCollecte", totalCollecte);
        model.addAttribute("actions", actions);
        return "organisation/dashboard";
    }

    @PostMapping("/actions/objectif/{id}")
    public String setObjectifCollecte(@PathVariable Long id, @RequestParam double valeur) {
        ActionCharite action = actionService.getActionChariteById(id).orElseThrow();
        action.setObjectifCollecte(valeur);
        double objectif = action.getObjectifCollecte();
        actionService.updateActionCharite(action);
        return "redirect:/organisation/actions";
    }

    @GetMapping("/profil")
    public String showProfil(Model model, Principal principal) {
        Utilisateur user = utilisateurService.getUserByEmail(principal.getName()).orElseThrow();
        Organisation organisation = organisationService.getOrganisationsByAdmin(user).get(0);
        Utilisateur admin = organisation.getAdmin();
        model.addAttribute("organisation", organisation);
        model.addAttribute("admin", admin);
        return "organisation/profil";
    }

    @PostMapping("/profil")
    public String updateProfil(
            @Valid @ModelAttribute("organisation") Organisation organisation,
            BindingResult result,
            @RequestParam("logoFile") MultipartFile logoFile,
            Authentication authentication,
            Model model) {
        Utilisateur user = utilisateurService.getUserByEmail(authentication.getName()).orElseThrow();
        Organisation org = organisationService.getOrganisationsByAdmin(user).get(0);

        if (result.hasErrors()) {
            model.addAttribute("admin", org.getAdmin());
            return "organisation/profil";
        }

        if (!logoFile.isEmpty()) {
            String contentType = logoFile.getContentType();
            if (!contentType.equals("image/jpeg") && !contentType.equals("image/png")) {
                throw new IllegalArgumentException("Format d'image non supporté");
            }
            String logoUrl = imageStorageService.saveLogo(logoFile);
            organisation.setLogo(logoUrl);
        } else {
            organisation.setLogo(org.getLogo());
        }
        organisation.setId(org.getId());
        organisation.setAdmin(org.getAdmin());
        organisationService.updateOrganisation(organisation);

        return "redirect:/organisation/profil?success";
    }

    @GetMapping("/debug/actions")
    @ResponseBody
    public String debugActions() {
        StringBuilder result = new StringBuilder();
        result.append("<h2>Toutes les actions avec organisation</h2>");
        
        List<ActionCharite> allWithOrg = actionService.findAllWithOrganisation();
        result.append("<p>Total actions avec organisation: ").append(allWithOrg.size()).append("</p>");
        
        result.append("<ul>");
        for (ActionCharite action : allWithOrg) {
            result.append("<li>")
                .append("Action ID: ").append(action.getId())
                .append(", Titre: ").append(action.getTitre())
                .append(", Organisation: ").append(action.getOrganisation().getId())
                .append(" (").append(action.getOrganisation().getNom()).append(")")
                .append("</li>");
        }
        result.append("</ul>");
        
        return result.toString();
    }

    @GetMapping("/debug/update-statuts")
    @ResponseBody
    public String updateMissingStatuts() {
        StringBuilder result = new StringBuilder();
        result.append("<h2>Mise à jour des statuts manquants</h2>");
        
        List<ActionCharite> allActions = actionService.getAllActionsCharite();
        int updated = 0;
        
        for (ActionCharite action : allActions) {
            if (action.getStatut() == null || action.getStatut().isEmpty()) {
                action.setStatut("active");
                actionService.updateActionCharite(action);
                updated++;
            }
        }
        
        result.append("<p>").append(updated).append(" actions mises à jour avec le statut 'active'</p>");
        return result.toString();
    }

    @GetMapping("/actions/{id}")
    public String showActionDetails(@PathVariable Long id, Model model, Authentication authentication) {
        ActionCharite action = actionService.getActionChariteById(id).orElseThrow();
        Utilisateur user = utilisateurService.getUserByEmail(authentication.getName()).orElseThrow();
        Organisation org = organisationService.getOrganisationsByAdmin(user).get(0);
        
        model.addAttribute("action", action);
        model.addAttribute("organisation", org);
        return "organisation/action-details";
    }

    @PostMapping("/actions/statut/{id}")
    public String updateStatut(@PathVariable Long id, @RequestParam String statut) {
        ActionCharite action = actionService.getActionChariteById(id).orElseThrow();
        action.setStatut(statut);
        actionService.updateActionCharite(action);
        return "redirect:/organisation/actions/" + id;
    }

    @GetMapping("/actions/{actionId}/participants")
    public String listParticipants(@PathVariable Long actionId, Model model, Authentication authentication) {
        ActionCharite action = actionService.getActionChariteById(actionId).orElseThrow();
        Utilisateur user = utilisateurService.getUserByEmail(authentication.getName()).orElseThrow();
        Organisation org = organisationService.getOrganisationsByAdmin(user).get(0);
        
        // Vérifier que l'action appartient bien à l'organisation
        if (!action.getOrganisation().getId().equals(org.getId())) {
            return "redirect:/organisation/actions";
        }
        
        List<Participation> participations = participationService.getParticipationsByAction(action);
        
        model.addAttribute("action", action);
        model.addAttribute("organisation", org);
        model.addAttribute("participations", participations);
        return "organisation/participants";
    }
    
    @PostMapping("/actions/{actionId}/participants/{participationId}/statut")
    public String updateParticipationStatut(
            @PathVariable Long actionId, 
            @PathVariable Long participationId,
            @RequestParam String statut) {
        
        participationService.updateParticipationStatut(participationId, statut);
        return "redirect:/organisation/actions/" + actionId + "/participants";
    }
    
    @PostMapping("/actions/{actionId}/participants/{participationId}/message")
    public String contactParticipant(
            @PathVariable Long actionId,
            @PathVariable Long participationId,
            @RequestParam String message) {
        
        Participation participation = participationService.getParticipationById(participationId).orElseThrow();
        // Logique d'envoi de message (email, notification, etc.)
        // À implémenter selon les besoins
        
        return "redirect:/organisation/actions/" + actionId + "/participants?messageEnvoye=true";
    }

    @GetMapping("/participants")
    public String listAllParticipants(Model model, Authentication authentication) {
        Utilisateur user = utilisateurService.getUserByEmail(authentication.getName()).orElseThrow();
        Organisation org = organisationService.getOrganisationsByAdmin(user).get(0);
        
        List<ActionCharite> actions = actionService.findByOrganisationIdDirect(org.getId());
        List<Participation> allParticipations = new ArrayList<>();
        
        for (ActionCharite action : actions) {
            List<Participation> participations = participationService.getParticipationsByAction(action);
            allParticipations.addAll(participations);
        }
        
        model.addAttribute("participations", allParticipations);
        model.addAttribute("organisation", org);
        return "organisation/all-participants";
    }

    @GetMapping("/dons")
    public String listDons(
            @RequestParam(required = false) String periode,
            @RequestParam(required = false) Long actionId,
            Model model, 
            Authentication authentication) {
        
        Utilisateur user = utilisateurService.getUserByEmail(authentication.getName()).orElseThrow();
        Organisation org = organisationService.getOrganisationsByAdmin(user).get(0);
        
        List<ActionCharite> actions = actionService.findByOrganisationIdDirect(org.getId());
        List<Don> dons;
        
        // Filtrage par action spécifique si demandé
        if (actionId != null) {
            ActionCharite action = actionService.getActionChariteById(actionId).orElseThrow();
            dons = donService.getDonsByAction(action);
        } else {
            // Sinon, récupérer tous les dons pour toutes les actions de l'organisation
            dons = new ArrayList<>();
            for (ActionCharite action : actions) {
                dons.addAll(donService.getDonsByAction(action));
            }
        }
        
        // Filtrage par période si demandé
        if (periode != null && !periode.isEmpty()) {
            LocalDate today = LocalDate.now();
            LocalDate startDate;
            
            switch (periode) {
                case "jour":
                    startDate = today;
                    break;
                case "semaine":
                    startDate = today.minusWeeks(1);
                    break;
                case "mois":
                    startDate = today.minusMonths(1);
                    break;
                case "trimestre":
                    startDate = today.minusMonths(3);
                    break;
                case "annee":
                    startDate = today.minusYears(1);
                    break;
                default:
                    startDate = null;
            }
            
            if (startDate != null) {
                final LocalDate filterStartDate = startDate;
                dons = dons.stream()
                        .filter(don -> don.getDateDon().toLocalDate().isEqual(filterStartDate) || 
                                        don.getDateDon().toLocalDate().isAfter(filterStartDate))
                        .collect(Collectors.toList());
            }
        }
        
        // Calculs des statistiques
        double totalMontant = dons.stream().mapToDouble(Don::getMontant).sum();
        double moyenneDon = dons.isEmpty() ? 0 : totalMontant / dons.size();
        int nombreDonateurs = (int) dons.stream().map(don -> don.getDonateur().getId()).distinct().count();
        
        // Préparation des données pour le graphique (dons par jour sur les 30 derniers jours)
        Map<String, Double> donsParJour = new LinkedHashMap<>();
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(30);
        
        for (int i = 0; i < 30; i++) {
            LocalDate date = startDate.plusDays(i);
            String dateStr = date.format(DateTimeFormatter.ofPattern("dd/MM"));
            donsParJour.put(dateStr, 0.0);
        }
        
        // Remplir les données réelles
        for (Don don : dons) {
            LocalDate donDate = don.getDateDon().toLocalDate();
            if (!donDate.isBefore(startDate) && !donDate.isAfter(endDate)) {
                String dateStr = donDate.format(DateTimeFormatter.ofPattern("dd/MM"));
                donsParJour.put(dateStr, donsParJour.getOrDefault(dateStr, 0.0) + don.getMontant());
            }
        }
        
        model.addAttribute("dons", dons);
        model.addAttribute("actions", actions);
        model.addAttribute("organisation", org);
        model.addAttribute("actionSelectionnee", actionId);
        model.addAttribute("periodeSelectionnee", periode);
        
        model.addAttribute("totalMontant", totalMontant);
        model.addAttribute("moyenneDon", moyenneDon);
        model.addAttribute("nombreDons", dons.size());
        model.addAttribute("nombreDonateurs", nombreDonateurs);
        
        model.addAttribute("donsParJour", donsParJour);
        
        return "organisation/donations";
    }
    
    @GetMapping("/dons/export")
    public void exportDons(
            @RequestParam(required = false) Long actionId,
            @RequestParam(required = false) String periode,
            HttpServletResponse response,
            Authentication authentication) throws IOException {
        
        Utilisateur user = utilisateurService.getUserByEmail(authentication.getName()).orElseThrow();
        Organisation org = organisationService.getOrganisationsByAdmin(user).get(0);
        
        // Même logique de filtrage que dans la méthode listDons
        List<ActionCharite> actions = actionService.findByOrganisationIdDirect(org.getId());
        List<Don> dons;
        
        if (actionId != null) {
            ActionCharite action = actionService.getActionChariteById(actionId).orElseThrow();
            dons = donService.getDonsByAction(action);
        } else {
            dons = new ArrayList<>();
            for (ActionCharite action : actions) {
                dons.addAll(donService.getDonsByAction(action));
            }
        }
        
        if (periode != null && !periode.isEmpty()) {
            LocalDate today = LocalDate.now();
            LocalDate startDate;
            
            switch (periode) {
                case "jour": startDate = today; break;
                case "semaine": startDate = today.minusWeeks(1); break;
                case "mois": startDate = today.minusMonths(1); break;
                case "trimestre": startDate = today.minusMonths(3); break;
                case "annee": startDate = today.minusYears(1); break;
                default: startDate = null;
            }
            
            if (startDate != null) {
                final LocalDate filterStartDate = startDate;
                dons = dons.stream()
                        .filter(don -> don.getDateDon().toLocalDate().isEqual(filterStartDate) || 
                                        don.getDateDon().toLocalDate().isAfter(filterStartDate))
                        .collect(Collectors.toList());
            }
        }
        
        // Configuration de la réponse pour le téléchargement du CSV
        response.setContentType("text/csv");
        response.setCharacterEncoding("UTF-8");
        
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=dons_" + LocalDate.now() + ".csv";
        response.setHeader(headerKey, headerValue);
        
        // Écriture du CSV
        PrintWriter writer = response.getWriter();
        
        // En-tête CSV
        writer.println("ID,Date,Montant,Donateur,Email,Action");
        
        // Données
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        for (Don don : dons) {
            writer.println(
                    don.getId() + "," +
                    don.getDateDon().format(formatter) + "," +
                    don.getMontant() + "," +
                    don.getDonateur().getPrenom() + " " + don.getDonateur().getNom() + "," +
                    don.getDonateur().getEmail() + "," +
                    don.getAction().getTitre()
            );
        }
    }

    @GetMapping("/notifications")
    public String showNotifications(Model model, Authentication authentication) {
        Utilisateur user = utilisateurService.getUserByEmail(authentication.getName()).orElseThrow();
        Organisation org = organisationService.getOrganisationsByAdmin(user).get(0);
        
        List<Notification> notifications = notificationService.getNotificationsByUtilisateur(user);
        int unreadCount = notificationService.getUnreadNotificationsByUtilisateur(user).size();
        
        model.addAttribute("notifications", notifications);
        model.addAttribute("unreadCount", unreadCount);
        model.addAttribute("organisation", org);
        return "organisation/notifications";
    }
    
    @PostMapping("/notifications/{id}/mark-read")
    public String markNotificationAsRead(@PathVariable Long id, Authentication authentication) {
        Utilisateur user = utilisateurService.getUserByEmail(authentication.getName()).orElseThrow();
        Notification notification = notificationService.getNotificationById(id).orElseThrow();
        
        // Vérifier que la notification appartient bien à l'utilisateur connecté
        if (notification.getUtilisateur().getId().equals(user.getId())) {
            notificationService.markAsRead(id);
        }
        
        return "redirect:/organisation/notifications";
    }
    
    @PostMapping("/notifications/mark-all-read")
    public String markAllNotificationsAsRead(Authentication authentication) {
        Utilisateur user = utilisateurService.getUserByEmail(authentication.getName()).orElseThrow();
        notificationService.markAllAsRead(user);
        return "redirect:/organisation/notifications";
    }

    /**
     * Affiche publiquement la liste des actions disponibles pour faire un don
     */
    @GetMapping("/actions/public/list")
    public String listPublicActions(
            @RequestParam(required = false) Long categorieId,
            @RequestParam(required = false) Boolean donate,
            Model model,
            HttpServletRequest request) {
        
        // Récupérer toutes les actions sans filtrer par statut
        List<ActionCharite> actions = actionService.getAllActionsCharite();
        
        // Appliquer le filtre par catégorie si spécifié
        if (categorieId != null) {
            actions = actions.stream()
                    .filter(action -> action.getCategorie() != null && action.getCategorie().getId().equals(categorieId))
                    .collect(Collectors.toList());
        }
        
        // Ajouter les catégories pour le filtre
        model.addAttribute("categories", categorieService.getAllCategories());
        model.addAttribute("selectedCategorieId", categorieId);
        model.addAttribute("actions", actions);
        model.addAttribute("currentPath", request.getRequestURI());
        
        // Si le paramètre donate est présent, afficher un message pour encourager les dons
        if (donate != null && donate) {
            model.addAttribute("showDonateMessage", true);
        }
        
        return "organisation/public-actions";
    }

    /**
     * Affiche publiquement les détails d'une action pour faire un don
     */
    @GetMapping("/actions/public/{id}")
    public String viewPublicActionDetails(
            @PathVariable Long id,
            @RequestParam(required = false) String success,
            @RequestParam(required = false) String error,
            Model model,
            HttpServletRequest request) {
        
        ActionCharite action = actionService.getActionChariteById(id).orElseThrow();
        
        // On affiche toutes les actions sans vérifier le statut
        // pour que le bouton Détails marche toujours
        
        if (success != null) {
            model.addAttribute("success", success);
        }
        
        if (error != null) {
            model.addAttribute("error", error);
        }
        
        model.addAttribute("action", action);
        model.addAttribute("currentPath", request.getRequestURI());
        return "organisation/public-action-details";
    }
}
