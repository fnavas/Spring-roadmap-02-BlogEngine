package com.fnavas.BlogEngine.dto;

public record UserRegisterRequest(
        String username,
        String password
) {
}
