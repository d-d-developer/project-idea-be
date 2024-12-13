package project_idea.idea.payloads.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

@Schema(description = "Data Transfer Object for creating a new role")
public record RoleCreateDTO(
    @Schema(
        description = "Name of the role",
        example = "MODERATOR",
        minLength = 3,
        maxLength = 50,
        required = true
    )
    @NotEmpty(message = "Role name is required")
    @Size(min = 3, max = 50, message = "Role name must be between 3 and 50 characters")
    String name,

    @Schema(
        description = "Description of the role's permissions and responsibilities",
        example = "Moderator role with content management privileges",
        maxLength = 255
    )
    @Size(max = 255, message = "Description cannot exceed 255 characters")
    String description
) {}
