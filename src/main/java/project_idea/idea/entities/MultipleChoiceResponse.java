package project_idea.idea.entities;

import com.fasterxml.jackson.annotation.*;
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
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class MultipleChoiceResponse {
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "survey_id")
    @JsonIgnore
    private MultipleChoiceSurvey survey;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private SocialProfile socialProfile;

    @ElementCollection
    @CollectionTable(name = "survey_selected_options")
    private List<String> selectedOptions = new ArrayList<>();
}
