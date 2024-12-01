package project_idea.idea.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class OpenEndedSurvey extends BaseSurvey {
    @OneToMany(mappedBy = "survey")
    @JsonManagedReference
    private List<OpenEndedResponse> responses = new ArrayList<>();
}
