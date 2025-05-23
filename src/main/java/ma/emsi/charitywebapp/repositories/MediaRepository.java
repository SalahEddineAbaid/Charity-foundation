package ma.emsi.charitywebapp.repositories;

import ma.emsi.charitywebapp.entities.ActionCharite;
import ma.emsi.charitywebapp.entities.Media;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MediaRepository extends JpaRepository<Media, Long> {
    List<Media> findByAction(ActionCharite action);
    List<Media> findByType(String type);
}