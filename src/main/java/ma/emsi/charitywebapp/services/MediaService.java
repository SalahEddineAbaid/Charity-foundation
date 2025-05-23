package ma.emsi.charitywebapp.services;

import ma.emsi.charitywebapp.entities.ActionCharite;
import ma.emsi.charitywebapp.entities.Media;

import java.util.List;
import java.util.Optional;

public interface MediaService {
    Media saveMedia(Media media);
    Media updateMedia(Media media);
    Optional<Media> getMediaById(Long id);
    List<Media> getAllMedias();
    List<Media> getMediasByAction(ActionCharite action);
    List<Media> getMediasByType(String type);
    void deleteMedia(Long id);
}