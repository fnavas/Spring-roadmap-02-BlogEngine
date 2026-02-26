package com.fnavas.blogengine.service;

import com.fnavas.blogengine.dto.response.CommentResponse;
import com.fnavas.blogengine.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    @Override
    public List<CommentResponse> getCommentsByPostId(Long postId) {
        return List.of();
    }
}
