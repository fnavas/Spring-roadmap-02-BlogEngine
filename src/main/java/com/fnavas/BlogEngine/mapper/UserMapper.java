package com.fnavas.BlogEngine.mapper;

import com.fnavas.BlogEngine.dto.UserResponse;
import com.fnavas.BlogEngine.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    public UserResponse toResponse(User user);
}
