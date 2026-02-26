package com.fnavas.blogengine.service;

import com.fnavas.blogengine.dto.response.CommentResponse;

import java.util.List;

public interface CommentService {
    List<CommentResponse> getCommentsByPostId(Long postId);
}
