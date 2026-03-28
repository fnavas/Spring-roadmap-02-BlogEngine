package com.fnavas.blogengine.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Author reference embedded in posts and comments")
public record AuthorResponse(
        @Schema(description = "Author username", example = "fnavas")
        String username
) {
}
