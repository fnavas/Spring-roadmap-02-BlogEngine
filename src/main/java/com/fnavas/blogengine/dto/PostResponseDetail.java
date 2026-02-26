package com.fnavas.blogengine.dto;

public record PostResponseDetail(
        Long id,
        String title,
        String content,
        UserResponse author,
        CommentResponse[] comments
) {
}
