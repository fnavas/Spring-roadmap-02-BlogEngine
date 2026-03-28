package com.fnavas.blogengine.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Comment creation or update request")
public record CommentRequest(
        @NotBlank(message = "Comment text is required")
        @Size(max = 1000, message = "Comment must not exceed 1000 characters")
        @Schema(description = "Comment text", example = "Great article! Very helpful.", maxLength = 1000)
        String text
) {
}
