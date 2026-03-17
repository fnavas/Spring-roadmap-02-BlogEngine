package com.fnavas.blogengine.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fnavas.blogengine.dto.request.PostCreateRequest;
import com.fnavas.blogengine.dto.response.PostDetailResponse;
import com.fnavas.blogengine.dto.response.PostResponse;
import com.fnavas.blogengine.security.CustomAccessDeniedHandler;
import com.fnavas.blogengine.security.CustomAuthEntryPoint;
import com.fnavas.blogengine.service.JwtService;
import com.fnavas.blogengine.service.PostService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostRestController.class)
class PostRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PostService postService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private AuthenticationProvider authenticationProvider;

    @MockitoBean
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    @MockitoBean
    private CustomAuthEntryPoint customAuthEntryPoint;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private PostResponse samplePostResponse() {
        return new PostResponse(1L, "Title", "Content", null, null);
    }

    @Test
    @WithMockUser
    void getAllPosts_shouldReturnsOk() throws Exception {
        Mockito.when(postService.getAllPosts(null, null)).thenReturn(List.of(samplePostResponse()));

        mockMvc.perform(get("/api/v1/posts")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("Title"))
                .andExpect(jsonPath("$[0].content").value("Content"));
    }

    @Test
    @WithMockUser
    void getPostById_shouldReturnsOk() throws Exception {
        Long id = 1L;
        PostDetailResponse mockDetail = new PostDetailResponse(1L, "Title", "Content", null, null);
        Mockito.when(postService.getPostById(id)).thenReturn(mockDetail);

        mockMvc.perform(get("/api/v1/posts/{id}", id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser
    void getPostsByAuthor_shouldReturnOk() throws Exception {
        String author = "author";
        Mockito.when(postService.getAllPosts(author, null)).thenReturn(List.of(samplePostResponse()));

        mockMvc.perform(get("/api/v1/posts")
                .param("author", author)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    @WithMockUser
    void getPostsByTitle_shouldReturnOk() throws Exception {
        String title = "title";
        Mockito.when(postService.getAllPosts(null, title)).thenReturn(List.of(samplePostResponse()));

        mockMvc.perform(get("/api/v1/posts")
                .param("title", title)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void createPost_withUserRole_shouldReturn201() throws Exception {
        PostCreateRequest request = new PostCreateRequest("New Title", "New Content");
        Mockito.when(postService.createPost(any(PostCreateRequest.class))).thenReturn(samplePostResponse());

        mockMvc.perform(post("/api/v1/posts")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Title"));
    }

    @Test
    void createPost_unauthenticated_shouldReturn403() throws Exception {
        PostCreateRequest request = new PostCreateRequest("New Title", "New Content");

        mockMvc.perform(post("/api/v1/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void updatePost_withUserRole_shouldReturn200() throws Exception {
        Long id = 1L;
        PostCreateRequest request = new PostCreateRequest("Updated Title", "Updated Content");
        Mockito.when(postService.updatePost(Mockito.eq(id), any(PostCreateRequest.class))).thenReturn(samplePostResponse());

        mockMvc.perform(put("/api/v1/posts/{id}", id)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void deletePost_withUserRole_shouldReturn204() throws Exception {
        Long id = 1L;
        Mockito.doNothing().when(postService).deletePost(id);

        mockMvc.perform(delete("/api/v1/posts/{id}", id)
                .with(csrf()))
                .andExpect(status().isNoContent());
    }
}
