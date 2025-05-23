package ma.emsi.charitywebapp.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "medias")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Media {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;
    private String url;
    private String description;

    @ManyToOne
    @JoinColumn(name = "action_id")
    private ActionCharite action;

    public Long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

    public String getDescription() {
        return description;
    }

    public ActionCharite getAction() {
        return action;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAction(ActionCharite action) {
        this.action = action;
    }
}