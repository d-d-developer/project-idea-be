package project_idea.idea.payloads.user;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserLoginResponseDTO(
    @Schema(
        description = "JWT access token for authentication",
        example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
        format = "jwt"
    )
    String accessToken
) {}
