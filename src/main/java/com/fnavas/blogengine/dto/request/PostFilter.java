package com.fnavas.blogengine.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Filter criteria for searching posts")
public record PostFilter(
        @Schema(description = "Filter by author username (case-insensitive partial match)", example = "fnavas")
        String author,

        @Schema(description = "Filter by post title (case-insensitive partial match)", example = "Spring")
        String title
) {
}
