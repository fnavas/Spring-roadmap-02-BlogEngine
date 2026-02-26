package com.fnavas.blogengine.dto.response;

public record PostDetailResponse(
        Long id,
        String title,
        String content,
        UserResponse author,
        CommentResponse[] comments
) {
}
