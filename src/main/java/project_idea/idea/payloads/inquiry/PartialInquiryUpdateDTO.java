package project_idea.idea.payloads.inquiry;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

import java.util.Set;
import java.util.UUID;

public record PartialInquiryUpdateDTO(
    @Schema(description = "Title of the inquiry", example = "Updated inquiry title")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    String title,

    @Schema(description = "Detailed description of the inquiry")
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    String description,

    @Schema(description = "Professional role being sought", example = "Senior Backend Developer")
    @Size(min = 3, max = 100, message = "Professional role must be between 3 and 100 characters")
    String professionalRole,

    @Schema(description = "Location of the position", example = "Remote - Europe")
    String location,

    @Schema(description = "Categories this inquiry belongs to")
    Set<UUID> categories,
    
    @Schema(description = "Whether this inquiry should be featured")
    Boolean featured,

    @Schema(description = "Language override (ISO 639-1 code)", example = "en")
    String language
) {}
