package com.fnavas.blogengine.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Login credentials")
public class AuthRequest {
    @NotBlank(message = "Username is required")
    @Schema(description = "Username", example = "fnavas")
    private String username;

    @NotBlank(message = "Password is required")
    @Schema(description = "User password", example = "1234")
    private String password;
}
