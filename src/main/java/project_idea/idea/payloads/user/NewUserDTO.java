package project_idea.idea.payloads.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Set;
import java.util.UUID;
import project_idea.idea.enums.UserType;

public record NewUserDTO(
    @Schema(
        description = "User's first name",
        example = "John",
        minLength = 2,
        maxLength = 40,
        required = true
    )
    @NotEmpty(message = "firstName is a required field!")
    @Size(min = 2, max = 40, message = "The firstName must be between 2 and 40 characters!")
    String firstName,
    
    @Schema(
        description = "User's last name",
        example = "Doe",
        minLength = 2,
        maxLength = 40,
        required = true
    )
    @NotEmpty(message = "lastName is a required field!")
    @Size(min = 2, max = 40, message = "The lastName must be between 2 and 40 characters!")
    String lastName,
    
    @Schema(
        description = "Unique username for the account. If not provided, will be auto-generated",
        example = "john_doe123",
        pattern = "^[a-zA-Z0-9_-]{3,30}$",
        minLength = 3,
        maxLength = 30
    )
    @Pattern(regexp = "^[a-zA-Z0-9_-]{3,30}$", 
        message = "Username must be 3-30 characters long and can only contain letters, numbers, underscores and hyphens")
    String username,
    
    @Schema(
        description = "User's email address. Must be unique in the system",
        example = "john.doe@example.com",
        required = true,
        format = "email"
    )
    @NotEmpty(message = "Email is a required field!")
    @Email(message = "The email you entered is not valid")
    String email,
    
    @Schema(
        description = "User's password. Will be encrypted before storage",
        example = "password123",
        minLength = 4,
        required = true,
        format = "password"
    )
    @NotEmpty(message = "Password is a required field!")
    @Size(min = 4, message = "The password must be at least 4 characters!")
    String password,
    
    @Schema(
        description = "User's preferred language (ISO 639-1 code)",
        example = "en",
        pattern = "^[a-z]{2}$"
    )
    @Pattern(regexp = "^[a-z]{2}$", message = "Language must be a valid ISO 639-1 code")
    String preferredLanguage,

    @Schema(
            description = "User type (PROFESSIONAL, CREATOR, or INVESTOR)",
            example = "CREATOR",
            required = true
    )
    UserType userType,
    
    @Schema(
        description = "Set of category IDs representing user's interests",
        example = "[\"550e8400-e29b-41d4-a716-446655440000\"]"
    )
    Set<UUID> interests
) {
}
