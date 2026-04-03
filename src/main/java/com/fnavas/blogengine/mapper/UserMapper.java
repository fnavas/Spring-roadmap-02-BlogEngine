package com.fnavas.blogengine.mapper;

import com.fnavas.blogengine.dto.request.UserRegisterRequest;
import com.fnavas.blogengine.dto.response.AuthorResponse;
import com.fnavas.blogengine.dto.response.UserResponse;
import com.fnavas.blogengine.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toResponse(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "role", ignore = true)
    User toEntity(UserRegisterRequest userRegisterRequest);

    AuthorResponse toAuthorResponse(User user);
}
