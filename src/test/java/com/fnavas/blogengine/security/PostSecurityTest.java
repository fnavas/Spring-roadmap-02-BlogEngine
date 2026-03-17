package com.fnavas.blogengine.security;

import com.fnavas.blogengine.entity.Post;
import com.fnavas.blogengine.entity.User;
import com.fnavas.blogengine.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostSecurityTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostSecurity postSecurity;

    private Post postOwnedBy(String username) {
        User author = new User();
        author.setUsername(username);
        Post post = new Post();
        post.setId(1L);
        post.setAuthor(author);
        return post;
    }

    @Test
    void isAuthor_userIsAuthor_returnsTrue() {
        Long postId = 1L;
        when(postRepository.findById(postId)).thenReturn(Optional.of(postOwnedBy("testuser")));

        assertTrue(postSecurity.isAuthor(postId, "testuser"));
        verify(postRepository, times(1)).findById(postId);
    }

    @Test
    void isAuthor_userIsNotAuthor_returnsFalse() {
        Long postId = 1L;
        when(postRepository.findById(postId)).thenReturn(Optional.of(postOwnedBy("otheruser")));

        assertFalse(postSecurity.isAuthor(postId, "testuser"));
        verify(postRepository, times(1)).findById(postId);
    }

    @Test
    void isAuthor_postNotFound_returnsFalse() {
        Long postId = 99L;
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertFalse(postSecurity.isAuthor(postId, "testuser"));
        verify(postRepository, times(1)).findById(postId);
    }
}
