package com.fnavas.blogengine.mapper;

import com.fnavas.blogengine.dto.PostCreateRequest;
import com.fnavas.blogengine.dto.PostResponse;
import com.fnavas.blogengine.entity.Post;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PostMapper {
    public PostResponse toResponse(Post post);
    public Post toEntity(PostCreateRequest postCreateRequest);
}
