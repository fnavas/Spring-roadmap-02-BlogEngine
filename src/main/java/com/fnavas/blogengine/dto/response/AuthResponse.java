package com.fnavas.blogengine.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Authentication response containing the JWT token")
public class AuthResponse {
    @Schema(description = "JWT Bearer token (valid for 1 hour)",
            example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJmbmF2YXMiLCJpYXQiOjE3...")
    private String token;
}
