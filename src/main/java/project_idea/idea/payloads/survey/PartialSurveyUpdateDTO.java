package project_idea.idea.payloads.survey;

import jakarta.validation.constraints.Size;
import java.util.List;

public record PartialSurveyUpdateDTO(
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    String title,

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    String description,

    Boolean allowMultipleAnswers,
    
    Boolean active,
    
    @Size(min = 2, message = "At least 2 options are required for multiple choice surveys")
    List<String> options
) {}
