package project_idea.idea.payloads.survey;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;

public record SurveyResponseDTO(
    @Schema(
        description = "Unique identifier of the survey being responded to",
        example = "123e4567-e89b-12d3-a456-426614174000",
        required = true
    )
    @NotNull(message = "Survey ID is required")
    UUID surveyId,
    
    @Schema(
        description = "Selected options for multiple choice surveys",
        example = "[\"Option A\", \"Option B\"]"
    )
    List<String> selectedOptions,
    
    @Schema(
        description = "Text response for open-ended surveys",
        example = "This is my detailed response to the survey question",
        maxLength = 1000
    )
    @Size(max = 1000, message = "Open-ended response cannot exceed 1000 characters")
    String openEndedResponse
) {}
