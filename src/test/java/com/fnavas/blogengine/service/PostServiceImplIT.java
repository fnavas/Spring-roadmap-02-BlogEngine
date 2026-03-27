package com.fnavas.blogengine.service;

import com.fnavas.blogengine.dto.request.PostCreateRequest;
import com.fnavas.blogengine.repository.PostRepository;
import com.fnavas.blogengine.repository.UserRepository;
import com.fnavas.blogengine.security.PostSecurity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
public class PostServiceImplIT {

    @Autowired
    private PostService postService;

    @MockitoBean
    private PostRepository postRepository;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private PostSecurity postSecurity;

    @Test
    @WithMockUser(roles = "GUEST")
    void  createPost_shouldThrowAccessDenied_whenUserHasInvalidRole() {
        PostCreateRequest postCreateRequest = new PostCreateRequest("Test Title", "Test Content");

        assertThrows(AccessDeniedException.class, () -> postService.createPost(postCreateRequest));

        verifyNoInteractions(postRepository);
        verifyNoInteractions(userRepository);
    }

    @Test
    @WithMockUser(roles = "GUEST")
    void updatePost_shouldThrowAccessDenied_whenUserHasInvalidRole() {
        PostCreateRequest postCreateRequest = new PostCreateRequest("Test Title", "Test Content");

        assertThrows(AccessDeniedException.class, () -> postService.updatePost(1L, postCreateRequest));

        verifyNoInteractions(postRepository);
        verifyNoInteractions(userRepository);
    }

    @Test
    @WithMockUser(roles = "GUEST")
    void deletePost_shouldThrowAccessDenied_whenUserHasInvalidRole() {
        assertThrows(AccessDeniedException.class, () -> postService.deletePost(1L));

        verifyNoInteractions(postRepository);
        verifyNoInteractions(userRepository);
    }
}
