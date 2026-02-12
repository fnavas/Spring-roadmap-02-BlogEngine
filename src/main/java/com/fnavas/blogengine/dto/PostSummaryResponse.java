package com.fnavas.blogengine.dto;

import java.time.LocalDateTime;

public record PostSummaryResponse(
        Long id,
        String title,
        String authorUsername,
        LocalDateTime createdAt
) {
}
