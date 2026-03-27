package com.fnavas.blogengine.service;

import com.fnavas.blogengine.dto.request.CommentRequest;
import com.fnavas.blogengine.repository.CommentRepository;
import com.fnavas.blogengine.repository.PostRepository;
import com.fnavas.blogengine.repository.UserRepository;
import com.fnavas.blogengine.security.CommentSecurity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
public class CommentServiceImplIT {

    @Autowired
    private CommentService commentService;

    @MockitoBean
    private CommentRepository commentRepository;

    @MockitoBean
    private PostRepository postRepository;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private CommentSecurity commentSecurity;

    @Test
    @WithMockUser(roles = "GUEST")
    void createComment_shouldThrowAccessDenied_whenUserHasInvalidRole() {
        CommentRequest request = new CommentRequest("Test comment");

        assertThrows(AccessDeniedException.class, () -> commentService.createComment(1L, request));

        verifyNoInteractions(commentRepository);
        verifyNoInteractions(postRepository);
        verifyNoInteractions(userRepository);
    }

    @Test
    @WithMockUser(roles = "GUEST")
    void updateComment_shouldThrowAccessDenied_whenUserHasInvalidRole() {
        CommentRequest request = new CommentRequest("Updated comment");

        assertThrows(AccessDeniedException.class, () -> commentService.updateComment(1L, request));

        verifyNoInteractions(commentRepository);
    }

    @Test
    @WithMockUser(roles = "GUEST")
    void deleteComment_shouldThrowAccessDenied_whenUserHasInvalidRole() {
        assertThrows(AccessDeniedException.class, () -> commentService.deleteComment(1L));

        verifyNoInteractions(commentRepository);
    }
}
