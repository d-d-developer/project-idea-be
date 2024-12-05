package project_idea.idea.payloads.survey;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record MultipleChoiceSurveyResponseDTO(
    @Schema(
        description = "List of selected options from the survey choices",
        example = "['Option A', 'Option B']",
        required = true
    )
    @NotEmpty(message = "Must select at least one option")
    @Size(min = 1, message = "Must select at least one option")
    List<String> selectedOptions
) {}
