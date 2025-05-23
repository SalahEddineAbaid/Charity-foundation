package ma.emsi.charitywebapp.services;

import ma.emsi.charitywebapp.entities.Don;
import ma.emsi.charitywebapp.entities.Participation;
import ma.emsi.charitywebapp.entities.UserActivity;
import ma.emsi.charitywebapp.entities.Utilisateur;
import ma.emsi.charitywebapp.entities.ActionCharite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class DataExportService {

    @Autowired
    private UtilisateurService utilisateurService;
    
    @Autowired
    private DonService donService;
    
    @Autowired
    private ParticipationService participationService;
    
    @Autowired
    private ActivityService activityService;
    
    // Export JSON
    public String generateUserDataJson(Long userId) {
        Utilisateur user = utilisateurService.getUserById(userId).orElseThrow();
        
        Map<String, Object> userData = new HashMap<>();
        
        // Informations de profil
        Map<String, Object> profile = new HashMap<>();
        profile.put("id", user.getId());
        profile.put("email", user.getEmail());
        profile.put("nom", user.getNom());
        profile.put("prenom", user.getPrenom());
        profile.put("telephone", user.getTelephone());
        profile.put("adresse", user.getAdresse());
        profile.put("dateCreation", user.getDateCreation());
        userData.put("profile", profile);
        
        // Dons
        List<Don> dons = donService.getDonsByUtilisateur(user);
        List<Map<String, Object>> donsData = dons.stream()
                .map(this::convertDonToMap)
                .collect(Collectors.toList());
        userData.put("dons", donsData);
        
        // Participations
        List<Participation> participations = participationService.getParticipationsByUtilisateur(user);
        List<Map<String, Object>> participationsData = participations.stream()
                .map(this::convertParticipationToMap)
                .collect(Collectors.toList());
        userData.put("participations", participationsData);
        
        // Journal d'activité
        List<UserActivity> activities = activityService.getUserActivities(user, 1000);
        List<Map<String, Object>> activitiesData = activities.stream()
                .map(this::convertActivityToMap)
                .collect(Collectors.toList());
        userData.put("activities", activitiesData);
        
        // Conversion en JSON
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(userData);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erreur lors de la génération du JSON", e);
        }
    }
    
    // Méthodes de conversion
    private Map<String, Object> convertDonToMap(Don don) {
        Map<String, Object> donMap = new HashMap<>();
        donMap.put("montant", don.getMontant());
        donMap.put("date", don.getDateDon());
        if (don.getAction() != null) {
            donMap.put("actionTitre", don.getAction().getTitre());
            donMap.put("actionId", don.getAction().getId());
        }
        return donMap;
    }
    
    // Méthode pour convertir une participation en Map
    private Map<String, Object> convertParticipationToMap(Participation participation) {
        Map<String, Object> participationMap = new HashMap<>();
        participationMap.put("id", participation.getId());
        participationMap.put("date", participation.getDateParticipation());
        participationMap.put("statut", participation.getStatut());
        
        if (participation.getAction() != null) {
            ActionCharite action = participation.getAction();
            participationMap.put("actionId", action.getId());
            participationMap.put("actionTitre", action.getTitre());
            participationMap.put("actionDate", action.getDateDebut());
        }
        
        return participationMap;
    }
    
    // Méthode pour convertir une activité en Map
    private Map<String, Object> convertActivityToMap(UserActivity activity) {
        Map<String, Object> activityMap = new HashMap<>();
        activityMap.put("type", activity.getActivityType());
        activityMap.put("description", activity.getDescription());
        activityMap.put("date", activity.getDate());
        activityMap.put("ip", activity.getIpAddress());
        activityMap.put("userAgent", activity.getUserAgent());
        return activityMap;
    }
}
