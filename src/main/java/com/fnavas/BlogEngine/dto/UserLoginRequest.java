package com.fnavas.BlogEngine.dto;

public record UserLoginRequest(
        String username,
        String password
) {
}
