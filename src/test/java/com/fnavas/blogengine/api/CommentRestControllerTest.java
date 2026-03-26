package com.fnavas.blogengine.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fnavas.blogengine.dto.request.CommentRequest;
import com.fnavas.blogengine.dto.response.AuthorResponse;
import com.fnavas.blogengine.dto.response.CommentResponse;
import com.fnavas.blogengine.security.CustomAccessDeniedHandler;
import com.fnavas.blogengine.security.CustomAuthEntryPoint;
import com.fnavas.blogengine.service.CommentService;
import com.fnavas.blogengine.service.JwtService;
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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentRestController.class)
class CommentRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CommentService commentService;

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

    private CommentResponse sampleCommentResponse() {
        return new CommentResponse(1L, "Test comment", new AuthorResponse("testuser"), null);
    }

    private Page<CommentResponse> sampleCommentPage() {
        return new PageImpl<>(List.of(sampleCommentResponse()));
    }

    @Test
    @WithMockUser
    void getCommentsByPostId_shouldReturnOk() throws Exception {
        Long postId = 1L;
        Mockito.when(commentService.getCommentsByPostId(eq(postId), any(Pageable.class))).thenReturn(sampleCommentPage());

        mockMvc.perform(get("/api/v1/posts/{postId}/comments", postId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()").value(1))
                .andExpect(jsonPath("$.content[0].text").value("Test comment"));
    }

    @Test
    @WithMockUser
    void createComment_withAuthenticatedUser_shouldReturn201() throws Exception {
        Long postId = 1L;
        CommentRequest request = new CommentRequest("New comment");
        Mockito.when(commentService.createComment(eq(postId), any(CommentRequest.class)))
                .thenReturn(sampleCommentResponse());

        mockMvc.perform(post("/api/v1/posts/{postId}/comments", postId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.text").value("Test comment"));
    }

    @Test
    void createComment_unauthenticated_shouldReturn403() throws Exception {
        Long postId = 1L;
        CommentRequest request = new CommentRequest("New comment");

        mockMvc.perform(post("/api/v1/posts/{postId}/comments", postId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void updateComment_withAuthenticatedUser_shouldReturn200() throws Exception {
        Long postId = 1L;
        Long commentId = 1L;
        CommentRequest request = new CommentRequest("Updated comment");
        CommentResponse updatedResponse = new CommentResponse(1L, "Updated comment", new AuthorResponse("testuser"), null);
        Mockito.when(commentService.updateComment(eq(commentId), any(CommentRequest.class))).thenReturn(updatedResponse);

        mockMvc.perform(put("/api/v1/posts/{postId}/comments/{commentId}", postId, commentId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Updated comment"));
    }

    @Test
    @WithMockUser
    void deleteComment_withAuthenticatedUser_shouldReturn204() throws Exception {
        Long postId = 1L;
        Long commentId = 1L;
        Mockito.doNothing().when(commentService).deleteComment(commentId);

        mockMvc.perform(delete("/api/v1/posts/{postId}/comments/{commentId}", postId, commentId)
                .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void createComment_withBlankText_shouldReturn400() throws Exception {
        Long postId = 1L;
        CommentRequest request = new CommentRequest("");

        mockMvc.perform(post("/api/v1/posts/{postId}/comments", postId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
