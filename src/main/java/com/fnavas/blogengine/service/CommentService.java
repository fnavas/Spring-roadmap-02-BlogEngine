package com.fnavas.blogengine.service;

import com.fnavas.blogengine.dto.request.CommentRequest;
import com.fnavas.blogengine.dto.response.CommentResponse;

import java.util.List;

public interface CommentService {
    List<CommentResponse> getCommentsByPostId(Long postId);
    CommentResponse createComment(Long postId, CommentRequest request);
    CommentResponse updateComment(Long commentId, CommentRequest request);
    void deleteComment(Long commentId);
}
