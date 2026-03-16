package com.fnavas.blogengine.mapper;

import com.fnavas.blogengine.dto.response.CommentResponse;
import com.fnavas.blogengine.entity.Comment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    CommentResponse toResponse(Comment comment);
}
