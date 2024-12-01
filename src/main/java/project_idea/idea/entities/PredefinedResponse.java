package project_idea.idea.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
public class PredefinedResponse extends BaseResponse {

    @ManyToOne
    @JsonBackReference
    private PredefinedSurvey survey;

    @ElementCollection
    @CollectionTable(name = "survey_selected_options")
    private List<String> selectedOptions = new ArrayList<>();
}
