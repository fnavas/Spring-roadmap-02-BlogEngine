package com.fnavas.BlogEngine.dto;

import java.time.LocalDateTime;

public record PostResponse(
        Long id,
        String title,
        String content,
        LocalDateTime createdAt,
        UserResponse author
) {
}
