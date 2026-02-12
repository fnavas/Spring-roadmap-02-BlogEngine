package com.fnavas.blogengine.dto;

public record PostCreateRequest(
        String title,
        String content
) {
}
