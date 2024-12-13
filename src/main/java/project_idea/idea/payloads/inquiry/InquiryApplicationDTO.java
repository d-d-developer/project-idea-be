package project_idea.idea.payloads.inquiry;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record InquiryApplicationDTO(
    @Schema(description = "Application message", example = "I am interested in this position and believe I am a great fit for the role.", required = true)
    @NotEmpty(message = "Message is required")
    @Size(max = 1000, message = "Message cannot exceed 1000 characters")
    String message
) {}
