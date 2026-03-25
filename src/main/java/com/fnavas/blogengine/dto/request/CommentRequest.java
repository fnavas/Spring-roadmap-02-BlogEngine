package com.fnavas.blogengine.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommentRequest(
        @NotBlank(message = "Comment text is required")
        @Size(max = 1000, message = "Comment must not exceed 1000 characters")
        String text
) {
}
