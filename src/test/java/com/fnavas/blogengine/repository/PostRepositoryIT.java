package com.fnavas.blogengine.repository;

import com.fnavas.blogengine.entity.Post;
import com.fnavas.blogengine.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DataJpaTest
public class PostRepositoryIT {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        postRepository.deleteAll();
        userRepository.deleteAll();

        User author = new User();
        author.setUsername("author");
        author.setPassword("password");
        userRepository.save(author);
    }

    @Test
    void findByAuthor_whenPostsFound_returnPosts() {
        User author = userRepository.findByUsername("author").orElseThrow();
        Post post = new Post();
        post.setTitle("Test Post");
        post.setContent("This is a test post.");
        post.setAuthor(author);
        postRepository.save(post);

        List<Post> postsFound = postRepository.findByAuthor(author);

        assertEquals(1, postsFound.size());
        assertEquals("Test Post", postsFound.get(0).getTitle());
        assertEquals("This is a test post.", postsFound.get(0).getContent());
    }

    @Test
    void findByAuthor_whenNotFound_returnEmptyPosts() {
        User author = userRepository.findByUsername("author").orElseThrow();

        List<Post> postsFound = postRepository.findByAuthor(author);

        assertEquals(0, postsFound.size());
    }

    @Test
    void findByTitleContainingIgnoreCase_whenPostsFound_returnPosts() {
        User author = userRepository.findByUsername("author").orElseThrow();
        Post post = new Post();
        post.setTitle("Test Post");
        post.setContent("This is a test post.");
        post.setAuthor(author);
        postRepository.save(post);

        List<Post> LowerCasePostsFound = postRepository.findByTitleContainingIgnoreCase("test");
        List<Post> UpperCasePostsFound = postRepository.findByTitleContainingIgnoreCase("TEST");

        assertEquals(1, LowerCasePostsFound.size());
        assertEquals(1, UpperCasePostsFound.size());
        assertEquals(LowerCasePostsFound, UpperCasePostsFound);
    }
}
