package com.fnavas.blogengine.service;

import com.fnavas.blogengine.dto.request.CommentRequest;
import com.fnavas.blogengine.dto.response.CommentResponse;
import com.fnavas.blogengine.entity.Comment;
import com.fnavas.blogengine.entity.Post;
import com.fnavas.blogengine.entity.User;
import com.fnavas.blogengine.exception.CommentNotFoundException;
import com.fnavas.blogengine.exception.PostNotFoundException;
import com.fnavas.blogengine.exception.UserNotFoundException;
import com.fnavas.blogengine.mapper.CommentMapper;
import com.fnavas.blogengine.repository.CommentRepository;
import com.fnavas.blogengine.repository.PostRepository;
import com.fnavas.blogengine.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<CommentResponse> getCommentsByPostId(Long postId, Pageable pageable) {
        log.info("[getCommentsByPostId]-Service request to get comments by postId");
        log.debug("[getCommentsByPostId]-postId: {}", postId);
        if (!postRepository.existsById(postId)) {
            throw new PostNotFoundException("Post not found with id: " + postId);
        }
        Page<Comment> comments = commentRepository.findByPostId(postId, pageable);
        return comments.map(commentMapper::toResponse);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public CommentResponse createComment(Long postId, CommentRequest request) {
        log.info("[createComment]-Service request to create comment");
        log.debug("[createComment]-postId: {}, request: {}", postId, request);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Objects.requireNonNull(authentication, "Authentication must not be null");
        String username = authentication.getName();

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found with id: " + postId));
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));

        Comment comment = commentMapper.toEntity(request);
        comment.setPost(post);
        comment.setAuthor(author);

        Comment saved = commentRepository.save(comment);
        log.info("[createComment]-Comment created successfully with id: {}", saved.getId());
        return commentMapper.toResponse(saved);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN') or @commentSecurity.isAuthor(#commentId, authentication.name)")
    public CommentResponse updateComment(Long commentId, CommentRequest request) {
        log.info("[updateComment]-Service request to update comment");
        log.debug("[updateComment]-commentId: {}, request: {}", commentId, request);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Comment not found with id: " + commentId));
        comment.setText(request.text());

        Comment updated = commentRepository.save(comment);
        log.info("[updateComment]-Comment updated successfully with id: {}", updated.getId());
        return commentMapper.toResponse(updated);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN') or @commentSecurity.isAuthor(#commentId, authentication.name)")
    public void deleteComment(Long commentId) {
        log.info("[deleteComment]-Service request to delete comment");
        log.debug("[deleteComment]-commentId: {}", commentId);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Comment not found with id: " + commentId));
        commentRepository.delete(comment);
        log.info("[deleteComment]-Comment deleted successfully");
    }
}
