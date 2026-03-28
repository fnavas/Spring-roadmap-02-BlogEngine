package com.fnavas.blogengine.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "User registration or update request")
public record UserRegisterRequest(
        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        @Schema(description = "Unique username", example = "johndoe", minLength = 3, maxLength = 50)
        String username,

        @NotBlank(message = "Password is required")
        @Size(min = 4, message = "Password must be at least 4 characters")
        @Schema(description = "Account password", example = "securePass123", minLength = 4)
        String password
) {
}
