package com.fnavas.BlogEngine.service;

import com.fnavas.BlogEngine.dto.PostCreateRequest;
import com.fnavas.BlogEngine.dto.PostResponse;
import com.fnavas.BlogEngine.entity.Post;
import com.fnavas.BlogEngine.entity.User;
import com.fnavas.BlogEngine.exception.PostNotFoundException;
import com.fnavas.BlogEngine.mapper.PostMapper;
import com.fnavas.BlogEngine.repository.PostRepository;
import com.fnavas.BlogEngine.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @Mock
    private PostRepository postRepository;
    @Mock
    UserRepository userRepository;
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

    @Test
    void createPost_returnPostResponse() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("admin");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        Post mockPost = samplePost();
        User mockUser = mock(User.class);
        PostCreateRequest mockRequest = new PostCreateRequest(mockPost.getTitle(), mockPost.getContent());
        PostResponse mockResponse = samplePostResponse();

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(mockUser));
        when(postMapper.toEntity(mockRequest)).thenReturn(mockPost);
        when(postRepository.save(any(Post.class))).thenReturn(mockPost);
        when(postMapper.toResponse(any(Post.class))).thenReturn(mockResponse);

        PostResponse response = postService.createPost(mockRequest);

        assertNotNull(response);
        assertEquals(mockResponse.id(), response.id());
        assertEquals(mockResponse.title(), response.title());
        assertEquals(mockResponse.content(), response.content());

        verify(userRepository, times(1)).findByUsername("admin");
        verify(postMapper, times(1)).toEntity(mockRequest);
        verify(postRepository, times(1)).save(any(Post.class));
        verify(postMapper, times(1)).toResponse(any(Post.class));
    }
}