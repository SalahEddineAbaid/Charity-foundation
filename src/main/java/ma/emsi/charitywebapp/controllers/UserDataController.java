package ma.emsi.charitywebapp.controllers;

import ma.emsi.charitywebapp.entities.Utilisateur;
import ma.emsi.charitywebapp.services.DataExportService;
import ma.emsi.charitywebapp.services.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping("/user")
public class UserDataController {

    @Autowired
    private DataExportService dataExportService;
    
    @Autowired
    private UtilisateurService utilisateurService;
    
    @GetMapping("/download-data")
    public void downloadUserData(HttpServletResponse response, Principal principal) throws IOException {
        Utilisateur user = utilisateurService.getUserByEmail(principal.getName()).orElseThrow();
        
        String jsonData = dataExportService.generateUserDataJson(user.getId());
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=user_data_" + user.getId() + ".json");
        
        response.getWriter().write(jsonData);
        response.getWriter().flush();
    }
}
