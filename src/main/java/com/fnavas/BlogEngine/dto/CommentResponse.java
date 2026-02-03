package com.fnavas.BlogEngine.dto;

import java.time.LocalDateTime;

public record CommentResponse(
        Long id,
        String text,
        String authorUsername,
        LocalDateTime createdAt
) {
}
