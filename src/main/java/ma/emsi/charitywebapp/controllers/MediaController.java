package ma.emsi.charitywebapp.controllers;

import ma.emsi.charitywebapp.entities.ActionCharite;
import ma.emsi.charitywebapp.entities.Media;
import ma.emsi.charitywebapp.services.ActionChariteService;
import ma.emsi.charitywebapp.services.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/medias")
public class MediaController {

    private final MediaService mediaService;
    private final ActionChariteService actionChariteService;

    @Autowired
    public MediaController(MediaService mediaService, ActionChariteService actionChariteService) {
        this.mediaService = mediaService;
        this.actionChariteService = actionChariteService;
    }

    @PostMapping
    public ResponseEntity<Media> createMedia(@RequestBody Media media) {
        Media savedMedia = mediaService.saveMedia(media);
        return new ResponseEntity<>(savedMedia, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Media>> getAllMedias() {
        List<Media> medias = mediaService.getAllMedias();
        return new ResponseEntity<>(medias, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Media> getMediaById(@PathVariable Long id) {
        Optional<Media> media = mediaService.getMediaById(id);
        return media.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<Media>> getMediasByType(@PathVariable String type) {
        List<Media> medias = mediaService.getMediasByType(type);
        return new ResponseEntity<>(medias, HttpStatus.OK);
    }

    @GetMapping("/action/{actionId}")
    public ResponseEntity<List<Media>> getMediasByAction(@PathVariable Long actionId) {
        Optional<ActionCharite> action = actionChariteService.getActionById(actionId);
        if (action.isPresent()) {
            List<Media> medias = mediaService.getMediasByAction(action.get());
            return new ResponseEntity<>(medias, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Media> updateMedia(@PathVariable Long id, @RequestBody Media media) {
        Optional<Media> existingMedia = mediaService.getMediaById(id);
        if (existingMedia.isPresent()) {
            media.setId(id);
            Media updatedMedia = mediaService.updateMedia(media);
            return new ResponseEntity<>(updatedMedia, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMedia(@PathVariable Long id) {
        Optional<Media> existingMedia = mediaService.getMediaById(id);
        if (existingMedia.isPresent()) {
            mediaService.deleteMedia(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
