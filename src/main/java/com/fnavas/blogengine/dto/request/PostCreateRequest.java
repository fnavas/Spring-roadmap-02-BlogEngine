package com.fnavas.blogengine.dto.request;

public record PostCreateRequest(
        String title,
        String content
) {
}
