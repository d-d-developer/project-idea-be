package project_idea.idea.payloads.survey;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public record NewSurveyDTO(
    @Schema(
        description = "Title of the survey",
        example = "I’m thinking of creating a cloud rental platform. Any ideas or suggestions?",
        minLength = 3,
        maxLength = 100,
        required = true
    )
    @NotEmpty(message = "Title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    String title,

    @Schema(
        description = "Detailed description of the survey",
        example = "Imagine renting a cloud to make it rain right over the house of someone who annoys you. Fun, right?",
        maxLength = 1000
    )
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    String description,

    @Schema(
        description = "Categories this survey belongs to",
        example = "[\"550e8400-e29b-41d4-a716-446655440000\"]"
    )
    Set<UUID> categories,
    
    @Schema(
        description = "Whether this survey should be featured",
        example = "true"
    )
    Boolean featured,

    @Schema(
        description = "For multiple choice surveys: whether multiple options can be selected",
        example = "true"
    )
    boolean allowMultipleAnswers,
    
    @Schema(
        description = "Type of survey: true for open-ended, false for multiple choice",
        example = "false",
        required = true
    )
    @NotNull(message = "Survey type must be specified")
    boolean isOpenEnded,
    
    @Schema(
        description = "List of options for multiple choice surveys",
        example = "[\"Option A\", \"Option B\", \"Option C\", \"Option D\"]")
    @Size(min = 2, message = "At least 2 options are required for multiple choice surveys")
    List<String> options
) {}
