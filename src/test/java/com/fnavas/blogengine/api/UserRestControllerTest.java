package com.fnavas.blogengine.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fnavas.blogengine.dto.request.UserRegisterRequest;
import com.fnavas.blogengine.dto.response.UserResponse;
import com.fnavas.blogengine.entity.Role;
import com.fnavas.blogengine.security.CustomAccessDeniedHandler;
import com.fnavas.blogengine.security.CustomAuthEntryPoint;
import com.fnavas.blogengine.service.JwtService;
import com.fnavas.blogengine.service.UserService;
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

@WebMvcTest(UserRestController.class)
class UserRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

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

    private UserResponse sampleUserResponse() {
        return new UserResponse(1L, "testuser", Role.ROLE_USER);
    }

    private Page<UserResponse> sampleUserPage() {
        return new PageImpl<>(List.of(sampleUserResponse()));
    }

    @Test
    @WithMockUser
    void getAllUsers_shouldReturnOk() throws Exception {
        Mockito.when(userService.getAllUsers(any(Pageable.class))).thenReturn(sampleUserPage());

        mockMvc.perform(get("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()").value(1))
                .andExpect(jsonPath("$.content[0].username").value("testuser"));
    }

    @Test
    @WithMockUser
    void searchByUsername_shouldReturnOk() throws Exception {
        Mockito.when(userService.searchByUsername(eq("testuser"), any(Pageable.class))).thenReturn(sampleUserPage());

        mockMvc.perform(get("/api/v1/users")
                .param("username", "testuser")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()").value(1))
                .andExpect(jsonPath("$.content[0].username").value("testuser"));
    }

    @Test
    @WithMockUser
    void getUserById_shouldReturnOk() throws Exception {
        Long id = 1L;
        Mockito.when(userService.getUserById(id)).thenReturn(sampleUserResponse());

        mockMvc.perform(get("/api/v1/users/{id}", id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    @WithMockUser
    void createUser_shouldReturn201() throws Exception {
        UserRegisterRequest request = new UserRegisterRequest("testuser", "password");
        Mockito.when(userService.createUser(any(UserRegisterRequest.class))).thenReturn(sampleUserResponse());

        mockMvc.perform(post("/api/v1/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void updateUser_withUserRole_shouldReturn200() throws Exception {
        Long id = 1L;
        UserRegisterRequest request = new UserRegisterRequest("newusername", "newpassword");
        Mockito.when(userService.updateUser(Mockito.eq(id), any(UserRegisterRequest.class)))
                .thenReturn(new UserResponse(id, "newusername", Role.ROLE_USER));

        mockMvc.perform(put("/api/v1/users/{id}", id)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("newusername"));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    void deleteUser_withUserRole_shouldReturn204() throws Exception {
        Long id = 1L;
        Mockito.doNothing().when(userService).deleteUser(id);

        mockMvc.perform(delete("/api/v1/users/{id}", id)
                .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void createUser_withBlankUsername_shouldReturn400() throws Exception {
        UserRegisterRequest request = new UserRegisterRequest("", "validpass");

        mockMvc.perform(post("/api/v1/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void createUser_withShortPassword_shouldReturn400() throws Exception {
        UserRegisterRequest request = new UserRegisterRequest("validuser", "ab");

        mockMvc.perform(post("/api/v1/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
