package com.fnavas.blogengine.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fnavas.blogengine.dto.request.AuthRequest;
import com.fnavas.blogengine.security.CustomAccessDeniedHandler;
import com.fnavas.blogengine.security.CustomAuthEntryPoint;
import com.fnavas.blogengine.service.JwtService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthRestController.class)
class AuthRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private AuthenticationProvider authenticationProvider;

    @MockitoBean
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    @MockitoBean
    private CustomAuthEntryPoint customAuthEntryPoint;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @WithMockUser
    void login_withValidCredentials_shouldReturnToken() throws Exception {
        AuthRequest request = new AuthRequest("testuser", "password");
        UserDetails userDetails = new User("testuser", "password", Collections.emptyList());

        Mockito.when(authenticationManager.authenticate(any())).thenReturn(null);
        Mockito.when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        Mockito.when(jwtService.generateToken(userDetails)).thenReturn("mocked.jwt.token");

        mockMvc.perform(post("/api/v1/auth")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mocked.jwt.token"));
    }

    @Test
    @WithMockUser
    void login_withInvalidCredentials_shouldReturn401() throws Exception {
        AuthRequest request = new AuthRequest("testuser", "wrongpassword");
        Mockito.when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));

        mockMvc.perform(post("/api/v1/auth")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"));
    }

    @Test
    @WithMockUser
    void login_withBlankUsername_shouldReturn400() throws Exception {
        AuthRequest request = new AuthRequest("", "password");

        mockMvc.perform(post("/api/v1/auth")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void login_withBlankPassword_shouldReturn400() throws Exception {
        AuthRequest request = new AuthRequest("testuser", "");

        mockMvc.perform(post("/api/v1/auth")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
