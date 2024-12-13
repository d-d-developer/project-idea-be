package project_idea.idea.payloads.user;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserLoginDTO(
    @Schema(
        description = "User's email address",
        example = "john.doe@example.com",
        required = true
    )
    String email,
    
    @Schema(
        description = "User's password",
        example = "password123",
        required = true
    )
    String password
) {}
