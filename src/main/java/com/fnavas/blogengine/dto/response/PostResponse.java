package com.fnavas.blogengine.dto.response;

import java.time.LocalDateTime;

public record PostResponse(
        Long id,
        String title,
        String content,
        LocalDateTime createdAt,
        AuthorResponse author
) {
}
