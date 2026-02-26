package com.fnavas.blogengine.dto.request;

public record UserRegisterRequest(
        String username,
        String password
) {
}
