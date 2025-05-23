package ma.emsi.charitywebapp.services;

import ma.emsi.charitywebapp.entities.Utilisateur;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class ProfileImageService {

    private final Path uploadDir;
    
    @Autowired
    private UtilisateurService utilisateurService;
    
    @Value("${app.profile-images.max-size:5242880}") // 5 MB par défaut
    private long maxFileSize;
    
    @Value("${app.profile-images.allowed-types:image/jpeg,image/png}")
    private List<String> allowedTypes;
    
    public ProfileImageService(@Value("${app.profile-images.upload-dir}") String uploadDirPath) {
        this.uploadDir = Paths.get(uploadDirPath);
        try {
            // Ensure directory exists
            if (Files.notExists(this.uploadDir)) {
                Files.createDirectories(this.uploadDir);
                System.out.println("Created profile images directory at: " + this.uploadDir.toAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("Failed to create profile images directory: " + e.getMessage());
            throw new RuntimeException("Impossible de créer le répertoire de stockage des images", e);
        }
    }
    
    public String storeProfileImage(MultipartFile file, Long userId) {
        // Validation
        validateImage(file);
        
        try {
            // Génération d'un nom de fichier unique
            String filename = userId + "_" + UUID.randomUUID() + getExtension(file.getOriginalFilename());
            Path targetLocation = this.uploadDir.resolve(filename);
            
            // Redimensionnement de l'image
            BufferedImage originalImage = ImageIO.read(file.getInputStream());
            BufferedImage resizedImage = resizeImage(originalImage, 200, 200);
            
            // Sauvegarde de l'image redimensionnée
            String formatName = getExtension(file.getOriginalFilename()).substring(1); // sans le point
            ImageIO.write(resizedImage, formatName, targetLocation.toFile());
            
            // Mise à jour de l'utilisateur
            Utilisateur user = utilisateurService.getUserById(userId).orElseThrow();
            user.setPhotoUrl("/user/avatar/" + filename);
            utilisateurService.updateUser(user);
            
            return filename;
        } catch (IOException e) {
            throw new RuntimeException("Impossible de stocker le fichier", e);
        }
    }
    
    private void validateImage(MultipartFile file) {
        // Vérifier si le fichier est vide
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Le fichier est vide");
        }
        
        // Vérifier la taille du fichier
        if (file.getSize() > maxFileSize) {
            throw new IllegalArgumentException("Le fichier dépasse la taille maximale autorisée");
        }
        
        // Vérifier le type MIME
        String contentType = file.getContentType();
        if (contentType == null || !allowedTypes.contains(contentType)) {
            throw new IllegalArgumentException("Type de fichier non autorisé");
        }
        
        // Vérification supplémentaire du contenu (magic bytes)
        try {
            byte[] bytes = file.getBytes();
            if (bytes.length >= 4) {
                if (isJpegFile(bytes) || isPngFile(bytes)) {
                    return; // OK
                }
            }
            throw new IllegalArgumentException("Format de fichier invalide");
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la lecture du fichier", e);
        }
    }
    
    private boolean isJpegFile(byte[] bytes) {
        return bytes[0] == (byte) 0xFF && 
               bytes[1] == (byte) 0xD8 && 
               bytes[bytes.length - 2] == (byte) 0xFF && 
               bytes[bytes.length - 1] == (byte) 0xD9;
    }
    
    private boolean isPngFile(byte[] bytes) {
        return bytes[0] == (byte) 0x89 && 
               bytes[1] == 'P' && 
               bytes[2] == 'N' && 
               bytes[3] == 'G';
    }
    
    private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        Image resultingImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        outputImage.getGraphics().drawImage(resultingImage, 0, 0, null);
        return outputImage;
    }
    
    private String getExtension(String filename) {
        return filename.substring(filename.lastIndexOf('.'));
    }
}
