package com.fnavas.blogengine.dto.response;

import java.time.LocalDateTime;

public record CommentResponse(
        Long id,
        String text,
        AuthorResponse author,
        LocalDateTime createdAt
) {
}
