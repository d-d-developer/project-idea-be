package project_idea.idea.payloads.survey;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record NewSurveyDTO(
    @NotEmpty(message = "Title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    String title,

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    String description,

    boolean allowMultipleAnswers,
    
    @NotNull(message = "Survey type must be specified")
    boolean isOpenEnded,
    
    @Size(min = 2, message = "At least 2 options are required for multiple choice surveys")
    List<String> options
) {}
