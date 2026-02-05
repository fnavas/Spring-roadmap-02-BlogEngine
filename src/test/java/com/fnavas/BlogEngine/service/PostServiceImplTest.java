package com.fnavas.BlogEngine.service;

import com.fnavas.BlogEngine.dto.PostResponse;
import com.fnavas.BlogEngine.entity.Post;
import com.fnavas.BlogEngine.exception.PostNotFoundException;
import com.fnavas.BlogEngine.mapper.PostMapper;
import com.fnavas.BlogEngine.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

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

    private Post samplePost() {
        Post post = new Post();
        post.setId(1L);
        post.setTitle("Sample Title");
        post.setContent("Sample Content");
        return post;
    }

    private PostResponse samplePostResponse() {
        return new PostResponse(1L, "Sample Title", "Sample Content", null, null);
    }

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

    @Test
    void getPostById_returnPostResponse() {
        Long id = 1L;
        Post mockPost = samplePost();
        PostResponse mockPostResponse = samplePostResponse();
        when(postRepository.findById(id)).thenReturn(Optional.of(mockPost));
        when(postMapper.toResponse(mockPost)).thenReturn(mockPostResponse);

        PostResponse postResponse = postService.getPostById(id);

        assertNotNull(postResponse);
        assertEquals(mockPostResponse.id(), postResponse.id());
        assertEquals(mockPostResponse.title(), postResponse.title());
        assertEquals(mockPostResponse.content(), postResponse.content());
        verify(postRepository, Mockito.times(1)).findById(id);
        verify(postMapper, Mockito.times(1)).toResponse(mockPost);
    }

    @Test
    void getPostById_notFound_returnPostNotFoundException() {
        Long id = 1L;
        when(postRepository.findById(id)).thenReturn(Optional.empty());

        PostNotFoundException exception = assertThrows(PostNotFoundException.class, () -> postService.getPostById(id));

        assertEquals("Post not found with id: " +  id, exception.getMessage());
        verify(postRepository, Mockito.times(1)).findById(id);
    }
}