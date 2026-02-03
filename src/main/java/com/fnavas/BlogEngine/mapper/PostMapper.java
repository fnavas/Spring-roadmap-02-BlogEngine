package com.fnavas.BlogEngine.mapper;

import com.fnavas.BlogEngine.dto.PostResponse;
import com.fnavas.BlogEngine.entity.Post;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PostMapper {
    public PostResponse toResponse(Post post);
}
