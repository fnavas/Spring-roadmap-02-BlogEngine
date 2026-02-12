package com.fnavas.blogengine.dto;

import com.fnavas.blogengine.entity.Role;

public record UserResponse(
        Long id,
        String username,
        Role role
){
}
