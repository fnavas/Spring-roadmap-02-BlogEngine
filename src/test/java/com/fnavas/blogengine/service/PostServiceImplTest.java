package com.fnavas.blogengine.service;

import com.fnavas.blogengine.dto.request.PostCreateRequest;
import com.fnavas.blogengine.dto.request.PostFilter;
import com.fnavas.blogengine.dto.response.PostDetailResponse;
import com.fnavas.blogengine.dto.response.PostResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import com.fnavas.blogengine.entity.Post;
import com.fnavas.blogengine.entity.User;
import com.fnavas.blogengine.exception.PostNotFoundException;
import com.fnavas.blogengine.exception.UserNotFoundException;

import com.fnavas.blogengine.mapper.PostMapper;
import com.fnavas.blogengine.repository.PostRepository;
import com.fnavas.blogengine.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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
        Page<Post> posts = new PageImpl<>(List.of(new Post(), new Post(), new Post(), new Post()));
        when(postRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(posts);
        when(postMapper
                .toResponse(any(Post.class)))
                .thenReturn(new PostResponse(null, null, null, null, null));

        Page<PostResponse> postResponses = postService.getAllPosts(new PostFilter(null, null), Pageable.unpaged());

        assertEquals(4, postResponses.getTotalElements());
        verify(postRepository, Mockito.times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void getPostById_returnPostResponse() {
        Long id = 1L;
        Post mockPost = samplePost();
        PostDetailResponse mockPostDetailResponse = new PostDetailResponse(1L, "Sample Title", "Sample Content", null, null);
        when(postRepository.findById(id)).thenReturn(Optional.of(mockPost));
        when(postMapper.toResponseDetail(mockPost)).thenReturn(mockPostDetailResponse);

        PostDetailResponse postResponse = postService.getPostById(id);

        assertNotNull(postResponse);
        assertEquals(mockPostDetailResponse.id(), postResponse.id());
        assertEquals(mockPostDetailResponse.title(), postResponse.title());
        assertEquals(mockPostDetailResponse.content(), postResponse.content());
        verify(postRepository, Mockito.times(1)).findById(id);
        verify(postMapper, Mockito.times(1)).toResponseDetail(mockPost);
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
    void createPost_adminRole_returnPostResponse() {
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

    @Test
    void createPost_userRole_returnPostResponse() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("user");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        Post mockPost = samplePost();
        User mockUser = mock(User.class);
        PostCreateRequest mockRequest = new PostCreateRequest(mockPost.getTitle(), mockPost.getContent());
        PostResponse mockResponse = samplePostResponse();

        when(userRepository.findByUsername("user")).thenReturn(Optional.of(mockUser));
        when(postMapper.toEntity(mockRequest)).thenReturn(mockPost);
        when(postRepository.save(any(Post.class))).thenReturn(mockPost);
        when(postMapper.toResponse(any(Post.class))).thenReturn(mockResponse);

        PostResponse response = postService.createPost(mockRequest);

        assertNotNull(response);
        assertEquals(mockResponse.id(), response.id());
        assertEquals(mockResponse.title(), response.title());
        assertEquals(mockResponse.content(), response.content());

        verify(userRepository, times(1)).findByUsername("user");
        verify(postMapper, times(1)).toEntity(mockRequest);
        verify(postRepository, times(1)).save(any(Post.class));
        verify(postMapper, times(1)).toResponse(any(Post.class));
    }

    @Test
    void createPost_userNotFound_returnUserNotFoundException() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("unknown");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        PostCreateRequest mockRequest = new PostCreateRequest("Title", "Content");
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> postService.createPost(mockRequest));

        assertEquals("User not found with username: unknown", exception.getMessage());
        verify(userRepository, times(1)).findByUsername("unknown");
    }

    @Test
    void updatePost_returnPostResponse() {
        Long id = 1L;
        PostCreateRequest mockRequest = new PostCreateRequest("Updated Title", "Updated Content");
        when(postRepository.findById(id)).thenReturn(Optional.of(samplePost()));
        when(postRepository.save(any(Post.class))).thenReturn(samplePost());
        when(postMapper.toResponse(any(Post.class))).thenReturn(samplePostResponse());

        PostResponse updatedResponse = postService.updatePost(id, mockRequest);

        assertNotNull(updatedResponse);
        verify(postRepository, times(1)).findById(id);
        verify(postRepository, times(1)).save(any(Post.class));
        verify(postMapper, times(1)).toResponse(any(Post.class));
    }

    @Test
    void updatePost_idNotFound_returnPostNotFoundException() {
        Long id = 1L;
        PostCreateRequest mockRequest = new PostCreateRequest("Updated Title", "Updated Content");
        when(postRepository.findById(id)).thenReturn(Optional.empty());

        PostNotFoundException exception = assertThrows(
                PostNotFoundException.class, () -> postService.updatePost(id, mockRequest));

        assertEquals("Post not found with id: " + id, exception.getMessage());
        verify(postRepository, times(1)).findById(id);
        verify(postRepository, times(0)).save(any(Post.class));
    }

    @Test
    void deletePost_success() {
        Long id = 1L;
        when(postRepository.findById(id)).thenReturn(Optional.of(samplePost()));

        assertDoesNotThrow(() -> postService.deletePost(id));

        verify(postRepository, times(1)).findById(id);
        verify(postRepository, times(1)).delete(any(Post.class));
    }

    @Test
    void deletePost_idNotFound_returnPostNotFoundException() {
        Long id = 1L;
        when(postRepository.findById(id)).thenReturn(Optional.empty());

        PostNotFoundException exception = assertThrows(
                PostNotFoundException.class, () -> postService.deletePost(id));

        assertEquals("Post not found with id: " + id, exception.getMessage());
        verify(postRepository, times(1)).findById(id);
        verify(postRepository, times(0)).delete(any(Post.class));
    }
}