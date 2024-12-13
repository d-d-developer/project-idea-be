package project_idea.idea.payloads.thread;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record NewThreadDTO(
    @Schema(
        description = "Title of the thread",
        example = "Discussion about cloud computing projects",
        minLength = 3,
        maxLength = 100,
        required = true
    )
    @NotEmpty(message = "Title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    String title,

    @Schema(
        description = "Description of the thread",
        example = "A collection of cloud computing related projects and ideas",
        maxLength = 1000
    )
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    String description
) {}
