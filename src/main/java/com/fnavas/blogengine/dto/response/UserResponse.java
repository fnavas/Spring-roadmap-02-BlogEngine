package com.fnavas.blogengine.dto.response;

import com.fnavas.blogengine.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "User public profile")
public record UserResponse(
        @Schema(description = "User ID", example = "1")
        Long id,
        @Schema(description = "Username", example = "fnavas")
        String username,
        @Schema(description = "User role", example = "ROLE_USER")
        Role role
){
}
