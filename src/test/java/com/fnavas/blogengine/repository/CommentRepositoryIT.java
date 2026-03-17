package com.fnavas.blogengine.repository;

import com.fnavas.blogengine.entity.Comment;
import com.fnavas.blogengine.entity.Post;
import com.fnavas.blogengine.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class CommentRepositoryIT {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    private Post savedPost;

    @BeforeEach
    void setUp() {
        commentRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();

        User author = new User();
        author.setUsername("author");
        author.setPassword("password");
        userRepository.save(author);

        Post post = new Post();
        post.setTitle("Test Post");
        post.setContent("Test content");
        post.setAuthor(author);
        savedPost = postRepository.save(post);
    }

    @Test
    void findByPostId_whenCommentsExist_returnComments() {
        User author = userRepository.findByUsername("author").orElseThrow();

        Comment comment1 = new Comment();
        comment1.setText("First comment");
        comment1.setPost(savedPost);
        comment1.setAuthor(author);
        commentRepository.save(comment1);

        Comment comment2 = new Comment();
        comment2.setText("Second comment");
        comment2.setPost(savedPost);
        comment2.setAuthor(author);
        commentRepository.save(comment2);

        List<Comment> comments = commentRepository.findByPostId(savedPost.getId());

        assertEquals(2, comments.size());
    }

    @Test
    void findByPostId_whenNoComments_returnEmptyList() {
        List<Comment> comments = commentRepository.findByPostId(savedPost.getId());

        assertTrue(comments.isEmpty());
    }

    @Test
    void findByPostId_onlyReturnsCommentsForGivenPost() {
        User author = userRepository.findByUsername("author").orElseThrow();

        Post otherPost = new Post();
        otherPost.setTitle("Other Post");
        otherPost.setContent("Other content");
        otherPost.setAuthor(author);
        postRepository.save(otherPost);

        Comment commentOnOtherPost = new Comment();
        commentOnOtherPost.setText("Comment on other post");
        commentOnOtherPost.setPost(otherPost);
        commentOnOtherPost.setAuthor(author);
        commentRepository.save(commentOnOtherPost);

        List<Comment> comments = commentRepository.findByPostId(savedPost.getId());

        assertTrue(comments.isEmpty());
    }
}
