package com.fnavas.blogengine.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Post summary (used in listings)")
public record PostResponse(
        @Schema(description = "Post ID", example = "1")
        Long id,
        @Schema(description = "Post title", example = "Getting Started with Spring Boot")
        String title,
        @Schema(description = "Post body content", example = "Spring Boot makes it easy to create stand-alone applications...")
        String content,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @Schema(description = "Creation timestamp", example = "2026-03-28 10:30:00")
        LocalDateTime createdAt,
        AuthorResponse author
) {
}
