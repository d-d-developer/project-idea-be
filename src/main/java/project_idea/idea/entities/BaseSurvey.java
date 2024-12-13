package project_idea.idea.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import project_idea.idea.enums.PostType;

@Entity
@Table(name = "surveys")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Getter
@Setter
@NoArgsConstructor
public abstract class BaseSurvey extends Post {
    @PrePersist
    protected void onCreate() {
        super.onCreate();
        if (getType() == null) {
            setType(PostType.SURVEY);
        }
    }
}
