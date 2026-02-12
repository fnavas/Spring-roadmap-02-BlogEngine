package com.fnavas.blogengine.dto;

public record UserLoginRequest(
        String username,
        String password
) {
}
