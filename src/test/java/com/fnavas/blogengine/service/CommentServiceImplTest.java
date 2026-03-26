package com.fnavas.blogengine.service;

import com.fnavas.blogengine.dto.request.CommentRequest;
import com.fnavas.blogengine.dto.response.AuthorResponse;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private CommentServiceImpl commentService;

    private Comment sampleComment() {
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Sample comment text");
        return comment;
    }

    private CommentResponse sampleCommentResponse() {
        return new CommentResponse(1L, "Sample comment text", new AuthorResponse("testuser"), null);
    }

    // --- getCommentsByPostId ---

    @Test
    void getCommentsByPostId_ok_returnsListOfCommentResponses() {
        Long postId = 1L;
        List<Comment> comments = List.of(sampleComment(), sampleComment());
        when(postRepository.existsById(postId)).thenReturn(true);
        when(commentRepository.findByPostId(postId)).thenReturn(comments);
        when(commentMapper.toResponse(any(Comment.class))).thenReturn(sampleCommentResponse());

        List<CommentResponse> result = commentService.getCommentsByPostId(postId);

        assertEquals(2, result.size());
        verify(postRepository, times(1)).existsById(postId);
        verify(commentRepository, times(1)).findByPostId(postId);
        verify(commentMapper, times(2)).toResponse(any(Comment.class));
    }

    @Test
    void getCommentsByPostId_postNotFound_throwsPostNotFoundException() {
        Long postId = 99L;
        when(postRepository.existsById(postId)).thenReturn(false);

        PostNotFoundException ex = assertThrows(PostNotFoundException.class,
                () -> commentService.getCommentsByPostId(postId));

        assertEquals("Post not found with id: " + postId, ex.getMessage());
        verify(commentRepository, never()).findByPostId(any());
    }

    // --- createComment ---

    @Test
    void createComment_ok_returnsCommentResponse() {
        Long postId = 1L;
        CommentRequest request = new CommentRequest("New comment");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testuser");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        Post mockPost = new Post();
        User mockUser = new User();
        Comment savedComment = sampleComment();
        CommentResponse mockResponse = sampleCommentResponse();

        when(postRepository.findById(postId)).thenReturn(Optional.of(mockPost));
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));
        when(commentMapper.toEntity(request)).thenReturn(new Comment());
        when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);
        when(commentMapper.toResponse(savedComment)).thenReturn(mockResponse);

        CommentResponse result = commentService.createComment(postId, request);

        assertNotNull(result);
        assertEquals(mockResponse.id(), result.id());
        assertEquals(mockResponse.text(), result.text());
        verify(postRepository, times(1)).findById(postId);
        verify(userRepository, times(1)).findByUsername("testuser");
        verify(commentMapper, times(1)).toEntity(request);
        verify(commentRepository, times(1)).save(any(Comment.class));
        verify(commentMapper, times(1)).toResponse(savedComment);
    }

    @Test
    void createComment_postNotFound_throwsPostNotFoundException() {
        Long postId = 99L;
        CommentRequest request = new CommentRequest("New comment");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testuser");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        PostNotFoundException ex = assertThrows(PostNotFoundException.class,
                () -> commentService.createComment(postId, request));

        assertEquals("Post not found with id: " + postId, ex.getMessage());
        verify(commentRepository, never()).save(any());
    }

    @Test
    void createComment_userNotFound_throwsUserNotFoundException() {
        Long postId = 1L;
        CommentRequest request = new CommentRequest("New comment");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("unknown");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(postRepository.findById(postId)).thenReturn(Optional.of(new Post()));
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        UserNotFoundException ex = assertThrows(UserNotFoundException.class,
                () -> commentService.createComment(postId, request));

        assertEquals("User not found with username: unknown", ex.getMessage());
        verify(commentRepository, never()).save(any());
    }

    // --- updateComment ---

    @Test
    void updateComment_ok_returnsUpdatedCommentResponse() {
        Long commentId = 1L;
        CommentRequest request = new CommentRequest("Updated text");
        Comment existing = sampleComment();
        Comment updated = sampleComment();
        updated.setText("Updated text");
        CommentResponse mockResponse = new CommentResponse(1L, "Updated text", new AuthorResponse("testuser"), null);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(existing));
        when(commentRepository.save(existing)).thenReturn(updated);
        when(commentMapper.toResponse(updated)).thenReturn(mockResponse);

        CommentResponse result = commentService.updateComment(commentId, request);

        assertNotNull(result);
        assertEquals("Updated text", result.text());
        verify(commentRepository, times(1)).findById(commentId);
        verify(commentRepository, times(1)).save(existing);
        verify(commentMapper, times(1)).toResponse(updated);
    }

    @Test
    void updateComment_commentNotFound_throwsCommentNotFoundException() {
        Long commentId = 99L;
        CommentRequest request = new CommentRequest("Updated text");
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        CommentNotFoundException ex = assertThrows(CommentNotFoundException.class,
                () -> commentService.updateComment(commentId, request));

        assertEquals("Comment not found with id: " + commentId, ex.getMessage());
        verify(commentRepository, never()).save(any());
    }

    // --- deleteComment ---

    @Test
    void deleteComment_ok_deletesComment() {
        Long commentId = 1L;
        Comment existing = sampleComment();
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(existing));

        assertDoesNotThrow(() -> commentService.deleteComment(commentId));

        verify(commentRepository, times(1)).findById(commentId);
        verify(commentRepository, times(1)).delete(existing);
    }

    @Test
    void deleteComment_commentNotFound_throwsCommentNotFoundException() {
        Long commentId = 99L;
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        CommentNotFoundException ex = assertThrows(CommentNotFoundException.class,
                () -> commentService.deleteComment(commentId));

        assertEquals("Comment not found with id: " + commentId, ex.getMessage());
        verify(commentRepository, never()).delete(any());
    }
}
