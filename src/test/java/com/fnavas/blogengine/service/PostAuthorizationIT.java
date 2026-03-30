package com.fnavas.blogengine.service;

import com.fnavas.blogengine.dto.request.CommentRequest;
import com.fnavas.blogengine.dto.request.PostCreateRequest;
import com.fnavas.blogengine.entity.Comment;
import com.fnavas.blogengine.entity.Post;
import com.fnavas.blogengine.entity.Role;
import com.fnavas.blogengine.entity.User;
import com.fnavas.blogengine.repository.CommentRepository;
import com.fnavas.blogengine.repository.PostRepository;
import com.fnavas.blogengine.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests verifying ownership-based authorization:
 * a USER cannot modify resources owned by another user.
 */
@SpringBootTest
@Transactional
class PostAuthorizationIT {

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Long authorPostId;
    private Long authorCommentId;

    @BeforeEach
    void setUp() {
        User author = new User();
        author.setUsername("author");
        author.setPassword(passwordEncoder.encode("pass"));
        author.setRole(Role.ROLE_USER);
        userRepository.save(author);

        Post post = new Post();
        post.setTitle("Author's Post");
        post.setContent("Original content");
        post.setAuthor(author);
        postRepository.save(post);
        authorPostId = post.getId();

        Comment comment = new Comment();
        comment.setText("Author's comment");
        comment.setPost(post);
        comment.setAuthor(author);
        commentRepository.save(comment);
        authorCommentId = comment.getId();
    }

    // --- Post authorization ---

    @Test
    @WithMockUser(username = "otherUser", roles = "USER")
    void updatePost_shouldThrowAccessDenied_whenUserIsNotAuthor() {
        PostCreateRequest request = new PostCreateRequest("Hacked Title", "Hacked content");

        assertThrows(AccessDeniedException.class,
                () -> postService.updatePost(authorPostId, request));
    }

    @Test
    @WithMockUser(username = "otherUser", roles = "USER")
    void deletePost_shouldThrowAccessDenied_whenUserIsNotAuthor() {
        assertThrows(AccessDeniedException.class,
                () -> postService.deletePost(authorPostId));
    }

    @Test
    @WithMockUser(username = "author", roles = "USER")
    void updatePost_shouldSucceed_whenUserIsAuthor() {
        PostCreateRequest request = new PostCreateRequest("Updated Title", "Updated content");

        assertDoesNotThrow(() -> postService.updatePost(authorPostId, request));
    }

    @Test
    @WithMockUser(username = "author", roles = "USER")
    void deletePost_shouldSucceed_whenUserIsAuthor() {
        assertDoesNotThrow(() -> postService.deletePost(authorPostId));
    }

    @Test
    @WithMockUser(username = "adminUser", roles = "ADMIN")
    void updatePost_shouldSucceed_whenUserIsAdmin() {
        PostCreateRequest request = new PostCreateRequest("Admin Updated", "Admin content");

        assertDoesNotThrow(() -> postService.updatePost(authorPostId, request));
    }

    // --- Comment authorization ---

    @Test
    @WithMockUser(username = "otherUser", roles = "USER")
    void updateComment_shouldThrowAccessDenied_whenUserIsNotAuthor() {
        CommentRequest request = new CommentRequest("Hacked comment");

        assertThrows(AccessDeniedException.class,
                () -> commentService.updateComment(authorCommentId, request));
    }

    @Test
    @WithMockUser(username = "otherUser", roles = "USER")
    void deleteComment_shouldThrowAccessDenied_whenUserIsNotAuthor() {
        assertThrows(AccessDeniedException.class,
                () -> commentService.deleteComment(authorCommentId));
    }

    @Test
    @WithMockUser(username = "author", roles = "USER")
    void updateComment_shouldSucceed_whenUserIsAuthor() {
        CommentRequest request = new CommentRequest("Updated comment text");

        assertDoesNotThrow(() -> commentService.updateComment(authorCommentId, request));
    }

    @Test
    @WithMockUser(username = "adminUser", roles = "ADMIN")
    void deleteComment_shouldSucceed_whenUserIsAdmin() {
        assertDoesNotThrow(() -> commentService.deleteComment(authorCommentId));
    }
}
