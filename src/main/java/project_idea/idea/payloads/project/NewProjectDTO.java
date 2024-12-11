package project_idea.idea.payloads.project;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import project_idea.idea.payloads.project.RoadmapStepDTO;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public record NewProjectDTO(
    @Schema(description = "Title of the project", example = "Cloud Storage Platform", required = true)
    @NotEmpty(message = "Title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    String title,

    @Schema(description = "Detailed description of the project", example = "Building a cloud storage platform")
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    String description,

    @Schema(description = "Categories this project belongs to")
    Set<UUID> categories,
    
    @Schema(description = "Whether this project should be featured")
    Boolean featured,

    @Schema(
        description = "Optional language override (ISO 639-1 code). If not provided, user's preferred language will be used",
        example = "en"
    )
    String language,

    @Schema(description = "Initial roadmap steps")
    @Size(min = 1, message = "At least one roadmap step is required")
    List<RoadmapStepDTO> roadmapSteps,

    @Schema(description = "Initial project participant profile IDs")
    Set<UUID> participantProfileIds
) {}
