package project_idea.idea.payloads.survey;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record SurveyResponseDTO(
    @NotNull(message = "Survey ID is required")
    UUID surveyId,
    
    List<String> selectedOptions,
    
    @Size(max = 1000, message = "Open-ended response cannot exceed 1000 characters")
    String openEndedResponse
) {}
