package com.fnavas.blogengine.dto;

public record UserRegisterRequest(
        String username,
        String password
) {
}
