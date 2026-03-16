package com.fnavas.blogengine.security;

import com.fnavas.blogengine.entity.Comment;
import com.fnavas.blogengine.entity.User;
import com.fnavas.blogengine.repository.CommentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentSecurityTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentSecurity commentSecurity;

    private Comment commentOwnedBy(String username) {
        User author = new User();
        author.setUsername(username);
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setAuthor(author);
        return comment;
    }

    @Test
    void isAuthor_userIsAuthor_returnsTrue() {
        Long commentId = 1L;
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(commentOwnedBy("testuser")));

        assertTrue(commentSecurity.isAuthor(commentId, "testuser"));
        verify(commentRepository, times(1)).findById(commentId);
    }

    @Test
    void isAuthor_userIsNotAuthor_returnsFalse() {
        Long commentId = 1L;
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(commentOwnedBy("otheruser")));

        assertFalse(commentSecurity.isAuthor(commentId, "testuser"));
        verify(commentRepository, times(1)).findById(commentId);
    }

    @Test
    void isAuthor_commentNotFound_returnsFalse() {
        Long commentId = 99L;
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertFalse(commentSecurity.isAuthor(commentId, "testuser"));
        verify(commentRepository, times(1)).findById(commentId);
    }
}
