package project_idea.idea.payloads.survey;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public record PartialSurveyUpdateDTO(
    @Schema(
        description = "Updated survey title",
        example = "Updated Customer Satisfaction Survey",
        minLength = 3,
        maxLength = 100
    )
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    String title,

    @Schema(
        description = "Updated survey description",
        example = "Help us improve our services by providing your feedback",
        maxLength = 1000
    )
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    String description,

    @Schema(
        description = "Updated set of category IDs for the survey",
        example = "[\"550e8400-e29b-41d4-a716-446655440000\"]"
    )
    Set<UUID> categories,

    @Schema(
        description = "Whether multiple options can be selected in a multiple choice survey",
        example = "true"
    )
    Boolean allowMultipleAnswers,

    @Schema(
        description = "Whether the survey should be featured",
        example = "true"
    )
    Boolean featured,

    @Schema(
        description = "Whether the survey is currently active",
        example = "true"
    )
    Boolean active,

    @Schema(
        description = "Updated list of options for multiple choice surveys",
        example = "[\"Option 1\", \"Option 2\", \"Option 3\"]"
    )
    @Size(min = 2, message = "At least 2 options are required for multiple choice surveys")
    List<String> options
) {}
