package ma.emsi.charitywebapp.controllers;

import ch.qos.logback.core.model.Model;
import ma.emsi.charitywebapp.entities.ActionCharite;
import ma.emsi.charitywebapp.entities.Participation;
import ma.emsi.charitywebapp.entities.Utilisateur;
import ma.emsi.charitywebapp.services.ActionChariteService;
import ma.emsi.charitywebapp.services.ParticipationService;
import ma.emsi.charitywebapp.services.UtilisateurService;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/participations")
public class ParticipationController {

    private final ParticipationService participationService;
    private final UtilisateurService userService;
    private final ActionChariteService actionChariteService;

    @Autowired
    public ParticipationController(ParticipationService participationService,
            UtilisateurService userService,
            ActionChariteService actionChariteService) {
        this.participationService = participationService;
        this.userService = userService;
        this.actionChariteService = actionChariteService;
    }

    @PostMapping
    public ResponseEntity<Participation> createParticipation(@RequestBody Participation participation) {
        Participation savedParticipation = participationService.saveParticipation(participation);
        return new ResponseEntity<>(savedParticipation, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Participation>> getAllParticipations() {
        List<Participation> participations = participationService.getAllParticipations();
        return new ResponseEntity<>(participations, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Participation> getParticipationById(@PathVariable Long id) {
        Optional<Participation> participation = participationService.getParticipationById(id);
        return participation.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Participation>> getParticipationsByUser(@PathVariable Long userId) {
        Optional<Utilisateur> user = userService.getUserById(userId);
        if (user.isPresent()) {
            List<Participation> participations = participationService.getParticipationsByUtilisateur(user.get());
            return new ResponseEntity<>(participations, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/action/{actionId}")
    public ResponseEntity<List<Participation>> getParticipationsByAction(@PathVariable Long actionId) {
        Optional<ActionCharite> action = actionChariteService.getActionById(actionId);
        if (action.isPresent()) {
            List<Participation> participations = participationService.getParticipationsByAction(action.get());
            return new ResponseEntity<>(participations, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/user/{userId}/action/{actionId}")
    public ResponseEntity<Participation> getParticipationByUserAndAction(
            @PathVariable Long userId,
            @PathVariable Long actionId) {
        Optional<Utilisateur> user = userService.getUserById(userId);
        Optional<ActionCharite> action = actionChariteService.getActionById(actionId);

        if (user.isPresent() && action.isPresent()) {
            Optional<Participation> participation = participationService
                    .getParticipationByUtilisateurAndAction(user.get(), action.get());
            return participation.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Participation> updateParticipation(@PathVariable Long id,
            @RequestBody Participation participation) {
        Optional<Participation> existingParticipation = participationService.getParticipationById(id);
        if (existingParticipation.isPresent()) {
            participation.setId(id);
            Participation updatedParticipation = participationService.updateParticipation(participation);
            return new ResponseEntity<>(updatedParticipation, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}/statut")
    public ResponseEntity<Participation> updateStatut(@PathVariable Long id, @RequestBody String statut) {
        Optional<Participation> existingParticipation = participationService.getParticipationById(id);
        if (existingParticipation.isPresent()) {
            Participation participation = existingParticipation.get();
            participation.setStatut(statut);
            Participation updatedParticipation = participationService.updateParticipation(participation);
            return new ResponseEntity<>(updatedParticipation, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParticipation(@PathVariable Long id) {
        Optional<Participation> participation = participationService.getParticipationById(id);
        if (participation.isPresent()) {
            participationService.deleteParticipation(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/user/participations")
    @PreAuthorize("hasAnyRole('UTILISATEUR','ORGANISATION')")
    public String userParticipations(org.springframework.ui.Model model,
            org.springframework.security.core.Authentication authentication) {
        Utilisateur user = userService.getUserByEmail(authentication.getName()).orElseThrow();
        List<Participation> participations = participationService.getParticipationsByUtilisateur(user);
        model.addAttribute("participations", participations);
        return "user/participations";
    }

    @GetMapping("/inscrire/{actionId}")
    @PreAuthorize("hasAnyRole('UTILISATEUR','ORGANISATION')")
    public String inscrireAction(@PathVariable Long actionId,
            org.springframework.security.core.Authentication authentication) {
        Utilisateur user = userService.getUserByEmail(authentication.getName()).orElseThrow();
        ActionCharite action = actionChariteService.getActionById(actionId).orElseThrow();
        // Vérifier si déjà inscrit
        if (participationService.getParticipationByUtilisateurAndAction(user, action).isEmpty()) {
            Participation participation = new Participation();
            participation.setUtilisateur(user);
            participation.setAction(action);
            participation.setStatut("Inscrit");
            participationService.saveParticipation(participation);
        }
        return "redirect:/user/participations";
    }
}
