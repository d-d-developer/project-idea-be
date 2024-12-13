package project_idea.idea.payloads.fundraiser;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

public record NewFundraiserDTO(
    @Schema(description = "Title of the fundraiser", example = "Help fund our new project", required = true)
    @NotEmpty(message = "Title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    String title,

    @Schema(description = "Detailed description of the fundraiser", example = "We need your help to make this happen!")
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    String description,

    @Schema(description = "Target amount to raise", example = "1000.00", required = true)
    @NotNull(message = "Target amount is required")
    @DecimalMin(value = "0.01", message = "Target amount must be greater than 0")
    BigDecimal targetAmount,

    @Schema(description = "Categories this fundraiser belongs to")
    Set<UUID> categories,
    
    @Schema(description = "Optional thread ID to associate with this fundraiser")
    UUID threadId,

    @Schema(description = "Whether this fundraiser should be featured")
    Boolean featured,

    @Schema(
        description = "Optional language override (ISO 639-1 code). If not provided, user's preferred language will be used",
        example = "en"
    )
    String language
) {}
