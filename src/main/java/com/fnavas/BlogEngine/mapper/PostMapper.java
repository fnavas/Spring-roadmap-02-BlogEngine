package com.fnavas.BlogEngine.mapper;

import com.fnavas.BlogEngine.dto.PostCreateRequest;
import com.fnavas.BlogEngine.dto.PostResponse;
import com.fnavas.BlogEngine.dto.UserResponse;
import com.fnavas.BlogEngine.entity.Post;
import com.fnavas.BlogEngine.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PostMapper {
    public PostResponse toResponse(Post post);

    public Post toEntity(PostCreateRequest postCreateRequest);
}
