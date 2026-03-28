package com.fnavas.blogengine.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Post creation or update request")
public record PostCreateRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 255, message = "Title must not exceed 255 characters")
        @Schema(description = "Post title", example = "Getting Started with Spring Boot")
        String title,

        @NotBlank(message = "Content is required")
        @Schema(description = "Post body content", example = "Spring Boot makes it easy to create stand-alone applications...")
        String content
) {
}
