package project_idea.idea.payloads.survey;

import io.swagger.v3.oas.annotations.media.Schema;
import project_idea.idea.entities.MultipleChoiceSurvey;
import java.util.Map;

@Schema(description = "Data Transfer Object containing survey details with response statistics")
public record SurveyWithStatsDTO(
    @Schema(
        description = "The multiple choice survey details",
        required = true
    )
    MultipleChoiceSurvey survey,

    @Schema(
        description = "Map of option to response count",
        example = "{\"Option A\": 10, \"Option B\": 5}",
        required = true
    )
    Map<String, Long> statistics,

    @Schema(
        description = "Total number of responses received",
        example = "15",
        required = true
    )
    long totalResponses
) {}
