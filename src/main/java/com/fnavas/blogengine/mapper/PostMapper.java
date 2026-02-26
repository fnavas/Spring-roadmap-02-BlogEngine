package com.fnavas.blogengine.mapper;

import com.fnavas.blogengine.dto.request.PostCreateRequest;
import com.fnavas.blogengine.dto.response.PostDetailResponse;
import com.fnavas.blogengine.dto.response.PostResponse;
import com.fnavas.blogengine.entity.Post;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PostMapper {
    PostResponse toResponse(Post post);
    PostDetailResponse toResponseDetail(Post post);
    Post toEntity(PostCreateRequest postCreateRequest);
}
