package com.fnavas.BlogEngine.dto;

public record PostCreateRequest(
        String title,
        String content
) {
}
