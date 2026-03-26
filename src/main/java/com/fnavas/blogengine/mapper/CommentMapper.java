package com.fnavas.blogengine.mapper;

import com.fnavas.blogengine.dto.request.CommentRequest;
import com.fnavas.blogengine.dto.response.CommentResponse;
import com.fnavas.blogengine.entity.Comment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    Comment toEntity(CommentRequest request);
    CommentResponse toResponse(Comment comment);
}
