package com.fnavas.blogengine.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Detailed post view including comments")
public record PostDetailResponse(
        @Schema(description = "Post ID", example = "1")
        Long id,
        @Schema(description = "Post title", example = "Getting Started with Spring Boot")
        String title,
        @Schema(description = "Post body content", example = "Spring Boot makes it easy to create stand-alone applications...")
        String content,
        AuthorResponse author,
        @Schema(description = "List of comments on this post")
        List<CommentResponse> comments
) {
}
