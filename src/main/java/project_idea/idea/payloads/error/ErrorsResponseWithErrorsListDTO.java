package project_idea.idea.payloads.error;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.Map;

public record ErrorsResponseWithErrorsListDTO(
    @Schema(
        description = "General error message",
        example = "Validation failed"
    )
    String message,

    @Schema(
        description = "Timestamp when the error occurred",
        example = "2024-03-14T15:30:00"
    )
    LocalDateTime timestamp,

    @Schema(
        description = "Map of field-specific validation errors",
        example = "{\"email\": \"Invalid email format\", \"password\": \"Password too short\"}"
    )
    Map<String, String> errors
) {}
