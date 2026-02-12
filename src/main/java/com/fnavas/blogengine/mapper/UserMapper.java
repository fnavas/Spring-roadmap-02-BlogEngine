package com.fnavas.blogengine.mapper;

import com.fnavas.blogengine.dto.UserRegisterRequest;
import com.fnavas.blogengine.dto.UserResponse;
import com.fnavas.blogengine.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    public UserResponse toResponse(User user);
    public User toEntity(UserRegisterRequest userRegisterRequest);

}
