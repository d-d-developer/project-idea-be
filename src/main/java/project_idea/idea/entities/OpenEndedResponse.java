package project_idea.idea.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class OpenEndedResponse extends BaseResponse {
    @ManyToOne
    @JsonBackReference
    private OpenEndedSurvey survey;

    @Column(length = 1000)
    private String response;
}
