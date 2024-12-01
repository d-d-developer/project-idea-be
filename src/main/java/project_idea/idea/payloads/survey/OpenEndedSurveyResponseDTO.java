package project_idea.idea.payloads.survey;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record OpenEndedSurveyResponseDTO(
    @NotEmpty(message = "Response cannot be empty")
    @Size(max = 1000, message = "Response cannot exceed 1000 characters")
    String response
) {}
