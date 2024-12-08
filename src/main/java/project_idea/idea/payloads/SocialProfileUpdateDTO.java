package project_idea.idea.payloads;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.Map;
import org.hibernate.validator.constraints.URL;

public record SocialProfileUpdateDTO(
    @Schema(
        description = "Username for the social profile",
        example = "john_doe123",
        pattern = "^[a-zA-Z0-9_-]{3,30}$",
        required = true
    )
    @NotNull(message = "Username is required")
    @Pattern(regexp = "^[a-zA-Z0-9_-]{3,30}$", 
        message = "Username must be 3-30 characters long and can only contain letters, numbers, underscores and hyphens")
    String username,
    
    @Schema(
        description = "User's first name",
        example = "John",
        minLength = 2, 
        maxLength = 40,
        required = true
    )
    @NotNull(message = "First name is required")
    @Size(min = 2, max = 40, message = "firstName must be between 2 and 40 characters")
    String firstName,
    
    @Schema(
        description = "User's last name",
        example = "Doe",
        minLength = 2,
        maxLength = 40
    )
    @Size(min = 2, max = 40, message = "lastName must be between 2 and 40 characters")
    String lastName,
    
    @Schema(
        description = "User's biography or description",
        example = "Software developer passionate about creating great user experiences",
        maxLength = 1000
    )
    @Size(max = 1000, message = "Bio cannot exceed 1000 characters")
    String bio,

    @Schema(
        description = "Map of platform names to profile URLs",
        example = "{\"github\": \"https://github.com/username\", \"linkedin\": \"https://linkedin.com/in/username\"}"
    )
    Map<String, @URL(message = "Must be a valid URL") String> links
) {}
