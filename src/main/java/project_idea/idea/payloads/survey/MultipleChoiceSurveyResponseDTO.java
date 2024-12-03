package project_idea.idea.payloads.survey;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record MultipleChoiceSurveyResponseDTO(
    @NotEmpty(message = "Must select at least one option")
    @Size(min = 1, message = "Must select at least one option")
    List<String> selectedOptions
) {}
