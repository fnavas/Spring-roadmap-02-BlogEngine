package com.fnavas.blogengine.dto.response;

import java.time.LocalDateTime;

public record CommentResponse(
        Long id,
        String text,
        UserResponse author,
        LocalDateTime createdAt
) {
}
