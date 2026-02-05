package com.fnavas.BlogEngine.dto;

import com.fnavas.BlogEngine.entity.Role;

public record UserResponse(
        Long id,
        String username,
        Role role
){
}
