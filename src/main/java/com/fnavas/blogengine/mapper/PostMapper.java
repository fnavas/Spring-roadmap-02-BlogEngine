package com.fnavas.blogengine.mapper;

import com.fnavas.blogengine.dto.request.PostCreateRequest;
import com.fnavas.blogengine.dto.response.PostDetailResponse;
import com.fnavas.blogengine.dto.response.PostResponse;
import com.fnavas.blogengine.entity.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PostMapper {
    PostResponse toResponse(Post post);
    PostDetailResponse toResponseDetail(Post post);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "comments", ignore = true)
    Post toEntity(PostCreateRequest postCreateRequest);
}
