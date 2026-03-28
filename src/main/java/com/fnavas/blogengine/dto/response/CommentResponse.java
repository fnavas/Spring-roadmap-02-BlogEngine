package com.fnavas.blogengine.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Comment data")
public record CommentResponse(
        @Schema(description = "Comment ID", example = "1")
        Long id,
        @Schema(description = "Comment text", example = "Great article! Very helpful.")
        String text,
        AuthorResponse author,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @Schema(description = "Creation timestamp", example = "2026-03-28 11:00:00")
        LocalDateTime createdAt
) {
}
