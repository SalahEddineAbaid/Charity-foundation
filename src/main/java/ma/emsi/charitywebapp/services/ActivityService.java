package ma.emsi.charitywebapp.services;

import jakarta.servlet.http.HttpServletRequest;
import ma.emsi.charitywebapp.entities.UserActivity;
import ma.emsi.charitywebapp.entities.Utilisateur;
import ma.emsi.charitywebapp.repositories.UserActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ActivityService {

    @Autowired
    private UserActivityRepository activityRepository;
    
    @Autowired
    private HttpServletRequest request;
    
    public void logActivity(Utilisateur user, String activityType, String description) {
        UserActivity activity = new UserActivity();
        activity.setUtilisateur(user);
        activity.setActivityType(activityType);
        activity.setDescription(description);
        activity.setDate(LocalDateTime.now());
        
        // Récupérer les informations de la requête si disponible
        if (request != null) {
            activity.setIpAddress(request.getRemoteAddr());
            activity.setUserAgent(request.getHeader("User-Agent"));
        }
        
        activityRepository.save(activity);
    }
    
    public List<UserActivity> getUserActivities(Utilisateur user, int limit) {
        return activityRepository.findByUtilisateurOrderByDateDesc(user, PageRequest.of(0, limit));
    }
}
