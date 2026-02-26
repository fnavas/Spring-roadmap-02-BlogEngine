package com.fnavas.blogengine.dto.response;

import com.fnavas.blogengine.entity.Role;

public record UserResponse(
        Long id,
        String username,
        Role role
){
}
