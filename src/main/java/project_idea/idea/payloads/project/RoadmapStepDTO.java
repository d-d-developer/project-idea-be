package project_idea.idea.payloads.project;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import project_idea.idea.entities.RoadmapStep.StepStatus;

public record RoadmapStepDTO(
    @Schema(description = "Title of the roadmap step", example = "Design Phase", required = true)
    @NotEmpty(message = "Step title is required")
    @Size(min = 3, max = 100, message = "Step title must be between 3 and 100 characters")
    String title,

    @Schema(description = "Detailed description of the step")
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    String description,

    @Schema(description = "Order index of the step in the roadmap", example = "1")
    int orderIndex,

    @Schema(description = "Current status of the step", example = "TODO")
    StepStatus status
) {}
