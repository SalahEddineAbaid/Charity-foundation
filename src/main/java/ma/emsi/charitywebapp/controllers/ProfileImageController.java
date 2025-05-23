package ma.emsi.charitywebapp.controllers;

import jakarta.annotation.PostConstruct;
import ma.emsi.charitywebapp.entities.Utilisateur;
import ma.emsi.charitywebapp.services.ProfileImageService;
import ma.emsi.charitywebapp.services.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;

@Controller
@RequestMapping("/user")
public class ProfileImageController {

    @Value("${app.profile-images.upload-dir}")
    private String uploadDirPath;
    
    private Path uploadDir;
    
    @Autowired
    private ProfileImageService imageService;
    
    @Autowired
    private UtilisateurService utilisateurService;
    
    @PostConstruct
    public void init() {
        this.uploadDir = Paths.get(uploadDirPath);
        // Ensure directory exists
        try {
            if (Files.notExists(this.uploadDir)) {
                Files.createDirectories(this.uploadDir);
                System.out.println("Created profile images directory at: " + this.uploadDir.toAbsolutePath());
            }
        } catch (Exception e) {
            System.err.println("Failed to create profile images directory: " + e.getMessage());
        }
    }
    
    @PostMapping("/upload-avatar")
    public String uploadAvatar(@RequestParam("file") MultipartFile file, Principal principal, RedirectAttributes attributes) {
        try {
            if (file.isEmpty()) {
                attributes.addFlashAttribute("error", "Le fichier est vide.");
                return "redirect:/user/profile";
            }
            
            System.out.println("Receiving file upload: " + file.getOriginalFilename() + ", size: " + file.getSize() + " bytes");
            
            Utilisateur user = utilisateurService.getUserByEmail(principal.getName()).orElseThrow();
            String filename = imageService.storeProfileImage(file, user.getId());
            
            System.out.println("Image stored successfully with filename: " + filename);
            attributes.addFlashAttribute("success", "Photo de profil mise à jour avec succès");
            
        } catch (Exception e) {
            System.err.println("Error uploading avatar: " + e.getMessage());
            e.printStackTrace();
            attributes.addFlashAttribute("error", "Erreur lors de l'upload: " + e.getMessage());
        }
        return "redirect:/user/profile";
    }
    
    @GetMapping("/avatar/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        try {
            Path file = this.uploadDir.resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            
            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(resource);
            } else {
                System.err.println("Avatar file not found or not readable: " + filename);
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            System.err.println("Error serving avatar file: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Default avatar endpoint as fallback
    @GetMapping("/avatar")
    @ResponseBody
    public ResponseEntity<Resource> defaultAvatar() {
        try {
            // First try to serve from uploaded files if the user has one
            // If not, serve a default avatar
            Path defaultAvatar = Paths.get("src/main/resources/static/assets/images/default-avatar.svg");
            if (Files.exists(defaultAvatar)) {
                Resource resource = new UrlResource(defaultAvatar.toUri());
                return ResponseEntity.ok()
                        .contentType(MediaType.valueOf("image/svg+xml"))
                        .body(resource);
            } else {
                System.err.println("Default avatar file not found at: " + defaultAvatar.toAbsolutePath());
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            System.err.println("Error serving default avatar: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
