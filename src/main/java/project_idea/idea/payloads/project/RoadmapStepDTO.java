package project_idea.idea.payloads.project;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import project_idea.idea.enums.ProgressStatus;
import java.util.UUID;

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
    ProgressStatus status,

    @Schema(description = "ID of linked post (Fundraiser or Inquiry)", example = "550e8400-e29b-41d4-a716-446655440000")
    UUID linkedPostId,

    boolean dependenciesFulfilled
) {}
