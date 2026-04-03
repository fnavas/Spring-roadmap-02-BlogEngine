package com.fnavas.blogengine.mapper;

import com.fnavas.blogengine.dto.request.CommentRequest;
import com.fnavas.blogengine.dto.response.CommentResponse;
import com.fnavas.blogengine.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "post", ignore = true)
    @Mapping(target = "author", ignore = true)
    Comment toEntity(CommentRequest request);

    CommentResponse toResponse(Comment comment);
}
