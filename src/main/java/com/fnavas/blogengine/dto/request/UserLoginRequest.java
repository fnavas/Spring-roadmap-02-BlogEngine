package com.fnavas.blogengine.dto.request;

public record UserLoginRequest(
        String username,
        String password
) {
}
