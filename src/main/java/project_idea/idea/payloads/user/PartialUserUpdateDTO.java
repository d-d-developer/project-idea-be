package project_idea.idea.payloads.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import java.util.Set;
import java.util.UUID;

public record PartialUserUpdateDTO(
    @Schema(
        description = "User's first name",
        example = "John",
        minLength = 2,
        maxLength = 40
    )
    String firstName,

    @Schema(
        description = "User's last name",
        example = "Doe",
        minLength = 2,
        maxLength = 40
    )
    @Size(min = 2, max = 40, message = "The lastName must be between 2 and 40 characters!")
    String lastName,

    @Schema(
        description = "Unique username for the account",
        example = "john_doe123",
        pattern = "^[a-zA-Z0-9_-]{3,30}$"
    )
    @Pattern(regexp = "^[a-zA-Z0-9_-]{3,30}$", message = "Username must be 3-30 characters long and can only contain letters, numbers, underscores and hyphens")
    String username,

    @Schema(
        description = "User's email address",
        example = "john.doe@example.com",
        format = "email"
    )
    @Email(message = "The email you entered is not valid")
    String email,

    @Schema(
        description = "User's password",
        example = "newPassword123",
        minLength = 4,
        format = "password"
    )
    @Size(min = 4, message = "The password must be at least 4 characters!")
    String password,

    @Schema(
        description = "Set of category IDs representing user's interests",
        example = "[\"550e8400-e29b-41d4-a716-446655440000\"]"
    )
    Set<UUID> interests,

    @Schema(
        description = "User's preferred language (ISO 639-1 code)",
        example = "en",
        pattern = "^[a-z]{2}$"
    )
    @Pattern(regexp = "^[a-z]{2}$", message = "Language must be a valid ISO 639-1 code")
    String preferredLanguage
) {}
