package com.fnavas.blogengine.mapper;

import com.fnavas.blogengine.dto.CommentResponse;
import com.fnavas.blogengine.entity.Comment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    public CommentResponse toResponse(Comment comment);
    public Comment toEntity(CommentResponse commentResponse);
}
