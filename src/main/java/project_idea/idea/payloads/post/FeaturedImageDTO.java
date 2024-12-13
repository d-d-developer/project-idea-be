package project_idea.idea.payloads.post;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

public record FeaturedImageDTO(
    @Schema(description = "Alternative text for the featured image", example = "A beautiful sunset over the mountains")
    @Size(max = 255, message = "Alt text cannot exceed 255 characters")
    String altText
) {}
