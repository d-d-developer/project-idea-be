package project_idea.idea.payloads.survey;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record OpenEndedSurveyResponseDTO(
    @Schema(
        description = "User's text response to the open-ended survey question",
        example = "This is my detailed response to the survey question",
        maxLength = 1000,
        required = true
    )
    @NotEmpty(message = "Response cannot be empty")
    @Size(max = 1000, message = "Response cannot exceed 1000 characters")
    String response
) {}
