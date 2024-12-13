package project_idea.idea.payloads.error;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

public record ErrorsResponseDTO(
    @Schema(
        description = "Error message describing what went wrong",
        example = "Invalid input provided"
    )
    String message,
    
    @Schema(
        description = "Timestamp when the error occurred",
        example = "2024-03-14T15:30:00"
    )
    LocalDateTime timestamp
) {}
