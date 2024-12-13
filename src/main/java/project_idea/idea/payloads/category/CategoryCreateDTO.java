package project_idea.idea.payloads.category;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

@Schema(description = "Data Transfer Object for creating a new category")
public record CategoryCreateDTO(
    @Schema(
        description = "Name of the category",
        example = "Technology",
        minLength = 3,
        maxLength = 50,
        required = true
    )
    @NotEmpty(message = "Category name is required")
    @Size(min = 3, max = 50, message = "Category name must be between 3 and 50 characters")
    String name,

    @Schema(
        description = "Description of the category",
        example = "Technology related content and surveys",
        maxLength = 255
    )
    @Size(max = 255, message = "Description cannot exceed 255 characters")
    String description
) {}
