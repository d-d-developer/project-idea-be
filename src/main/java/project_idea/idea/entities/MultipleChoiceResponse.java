package project_idea.idea.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "MultipleChoice_responses")
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class MultipleChoiceResponse {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "survey_id")
    @JsonBackReference
    private MultipleChoiceSurvey survey;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private SocialProfile socialProfile;

    @ElementCollection
    @CollectionTable(name = "survey_selected_options")
    private List<String> selectedOptions = new ArrayList<>();
}
