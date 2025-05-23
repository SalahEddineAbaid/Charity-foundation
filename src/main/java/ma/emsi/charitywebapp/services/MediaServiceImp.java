package ma.emsi.charitywebapp.services;

import ma.emsi.charitywebapp.entities.ActionCharite;
import ma.emsi.charitywebapp.entities.Media;
import ma.emsi.charitywebapp.repositories.MediaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MediaServiceImp implements MediaService {

    private final MediaRepository mediaRepository;

    @Autowired
    public MediaServiceImp(MediaRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
    }

    @Override
    public Media saveMedia(Media media) {
        return mediaRepository.save(media);
    }

    @Override
    public Media updateMedia(Media media) {
        return mediaRepository.save(media);
    }

    @Override
    public Optional<Media> getMediaById(Long id) {
        return mediaRepository.findById(id);
    }

    @Override
    public List<Media> getAllMedias() {
        return mediaRepository.findAll();
    }

    @Override
    public List<Media> getMediasByAction(ActionCharite action) {
        return mediaRepository.findByAction(action);
    }

    @Override
    public List<Media> getMediasByType(String type) {
        return mediaRepository.findByType(type);
    }

    @Override
    public void deleteMedia(Long id) {
        mediaRepository.deleteById(id);
    }
}