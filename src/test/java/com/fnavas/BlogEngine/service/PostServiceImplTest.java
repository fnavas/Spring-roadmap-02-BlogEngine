package com.fnavas.BlogEngine.service;

import com.fnavas.BlogEngine.dto.PostResponse;
import com.fnavas.BlogEngine.entity.Post;
import com.fnavas.BlogEngine.mapper.PostMapper;
import com.fnavas.BlogEngine.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostMapper postMapper;

    @InjectMocks
    private PostServiceImpl postService;

    @Test
    void getAllPosts_returnAllTasks() {
        List<Post> posts = List.of(new Post(), new Post(), new Post(), new Post());
        when(postRepository.findAll()).thenReturn(posts);
        when(postMapper
                .toResponse(any(Post.class)))
                .thenReturn(new PostResponse(null, null, null, null, null));

        List<PostResponse> postResponses = postService.getAllPosts();

        assertEquals(4, postResponses.size());
        verify(postRepository, Mockito.times(1)).findAll();
    }
}