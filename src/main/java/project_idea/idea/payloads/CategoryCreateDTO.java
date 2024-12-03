package project_idea.idea.payloads;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record CategoryCreateDTO(
    @NotEmpty(message = "Category name is required")
    @Size(min = 3, max = 50, message = "Category name must be between 3 and 50 characters")
    String name,

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    String description
) {}
