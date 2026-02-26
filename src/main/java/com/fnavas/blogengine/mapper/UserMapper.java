package com.fnavas.blogengine.mapper;

import com.fnavas.blogengine.dto.request.UserRegisterRequest;
import com.fnavas.blogengine.dto.response.AuthorResponse;
import com.fnavas.blogengine.dto.response.UserResponse;
import com.fnavas.blogengine.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toResponse(User user);
    User toEntity(UserRegisterRequest userRegisterRequest);
    AuthorResponse toAuthorResponse(User user);

}
