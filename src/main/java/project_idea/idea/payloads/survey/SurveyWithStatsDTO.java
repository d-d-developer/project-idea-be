package project_idea.idea.payloads.survey;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import project_idea.idea.entities.MultipleChoiceSurvey;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SurveyWithStatsDTO {
    private MultipleChoiceSurvey survey;
    private Map<String, Long> statistics;
    private long totalResponses;
}
