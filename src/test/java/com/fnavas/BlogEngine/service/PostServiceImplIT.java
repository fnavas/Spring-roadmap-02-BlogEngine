package com.fnavas.BlogEngine.service;

import com.fnavas.BlogEngine.dto.PostCreateRequest;
import com.fnavas.BlogEngine.repository.PostRepository;
import com.fnavas.BlogEngine.repository.UserRepository;
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

    @MockitoBean(name = "postSecurity")
    private Object postSecurity;

    @Test
    @WithMockUser(roles = "GUEST")
    void  createPost_shouldThrowAccessDenied_whenUserHasInvalidRole() {
        PostCreateRequest postCreateRequest = new PostCreateRequest("Test Title", "Test Content");

        assertThrows(AccessDeniedException.class, () -> postService.createPost(postCreateRequest));

        verifyNoInteractions(postRepository);
        verifyNoInteractions(userRepository);
    }
}
