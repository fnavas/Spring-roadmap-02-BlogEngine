package com.fnavas.blogengine.dto.response;

import java.util.List;

public record PostDetailResponse(
        Long id,
        String title,
        String content,
        AuthorResponse author,
        List<CommentResponse> comments
) {
}
