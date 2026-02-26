package com.fnavas.blogengine.dto.response;

public record PostDetailResponse(
        Long id,
        String title,
        String content,
        AuthorResponse author,
        CommentResponse[] comments
) {
}
