package project_idea.idea.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class PredefinedSurvey extends BaseSurvey {
    private boolean allowMultipleAnswers = false;

    @ElementCollection
    @CollectionTable(name = "survey_options")
    private List<String> options = new ArrayList<>();

    @OneToMany(mappedBy = "survey")
    @JsonManagedReference
    private List<PredefinedResponse> responses = new ArrayList<>();
}
