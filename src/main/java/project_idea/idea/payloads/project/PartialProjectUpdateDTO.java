package project_idea.idea.payloads.project;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public record PartialProjectUpdateDTO(
    @Schema(description = "Title of the project", example = "Updated Cloud Storage Platform")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    String title,

    @Schema(description = "Detailed description of the project")
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    String description,

    @Schema(description = "Categories this project belongs to")
    Set<UUID> categories,
    
    @Schema(description = "Whether this project should be featured")
    Boolean featured,

    @Schema(
        description = "Language override (ISO 639-1 code)",
        example = "en"
    )
    String language,

    @Schema(description = "Project participant profile IDs")
    Set<UUID> participantProfileIds,

    @Schema(description = "Updated roadmap steps")
    List<RoadmapStepDTO> roadmapSteps
) {}
