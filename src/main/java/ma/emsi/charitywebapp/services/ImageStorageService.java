package ma.emsi.charitywebapp.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class ImageStorageService {

    public String saveLogo(MultipartFile file) {
        if (file.isEmpty())
            return null;
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path uploadPath = Paths.get("uploads/logos");
        try {
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Files.copy(file.getInputStream(), uploadPath.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/logos/" + filename; // URL accessible via un mapping statique
        } catch (IOException e) {
            throw new RuntimeException("Erreur upload logo", e);
        }
    }
}
