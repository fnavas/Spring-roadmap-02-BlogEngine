package com.fnavas.blogengine.api;

import com.fnavas.blogengine.dto.request.AuthRequest;
import com.fnavas.blogengine.dto.request.CommentRequest;
import com.fnavas.blogengine.dto.request.PostCreateRequest;
import com.fnavas.blogengine.dto.request.UserRegisterRequest;
import com.fnavas.blogengine.dto.response.AuthResponse;
import com.fnavas.blogengine.dto.response.CommentResponse;
import com.fnavas.blogengine.dto.response.PostDetailResponse;
import com.fnavas.blogengine.dto.response.PostResponse;
import com.fnavas.blogengine.dto.response.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end integration test covering the full user flow:
 * register → login → create post → read post → comment → read comments
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BlogEngineFlowIT {

    @LocalServerPort
    private int port;

    private final RestTemplate restTemplate = new RestTemplate();
    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;
    }

    @Test
    void fullFlow_register_login_createPost_comment() {
        // 1. Register a new user
        UserRegisterRequest registerRequest = new UserRegisterRequest("flowuser", "pass1234");
        ResponseEntity<UserResponse> registerResponse = restTemplate.postForEntity(
                baseUrl + "/api/v1/users", registerRequest, UserResponse.class);

        assertEquals(HttpStatus.CREATED, registerResponse.getStatusCode());
        assertNotNull(registerResponse.getBody());
        assertEquals("flowuser", registerResponse.getBody().username());
        assertNotNull(registerResponse.getBody().id());

        // 2. Login and obtain JWT token
        AuthRequest authRequest = new AuthRequest("flowuser", "pass1234");
        ResponseEntity<AuthResponse> authResponse = restTemplate.postForEntity(
                baseUrl + "/api/v1/auth", authRequest, AuthResponse.class);

        assertEquals(HttpStatus.OK, authResponse.getStatusCode());
        assertNotNull(authResponse.getBody());
        String token = authResponse.getBody().getToken();
        assertNotNull(token);
        assertFalse(token.isBlank());

        // Build auth headers for subsequent requests
        HttpHeaders authHeaders = new HttpHeaders();
        authHeaders.setBearerAuth(token);
        authHeaders.set("Content-Type", "application/json");

        // 3. Create a post with the authenticated user
        PostCreateRequest postRequest = new PostCreateRequest(
                "My First Post", "This is the content of my first blog post.");
        ResponseEntity<PostResponse> postResponse = restTemplate.exchange(
                baseUrl + "/api/v1/posts", HttpMethod.POST,
                new HttpEntity<>(postRequest, authHeaders), PostResponse.class);

        assertEquals(HttpStatus.CREATED, postResponse.getStatusCode());
        assertNotNull(postResponse.getBody());
        assertEquals("My First Post", postResponse.getBody().title());
        Long postId = postResponse.getBody().id();
        assertNotNull(postId);

        // 4. Read the post (public endpoint, no auth needed)
        ResponseEntity<PostDetailResponse> getPostResponse = restTemplate.getForEntity(
                baseUrl + "/api/v1/posts/" + postId, PostDetailResponse.class);

        assertEquals(HttpStatus.OK, getPostResponse.getStatusCode());
        assertNotNull(getPostResponse.getBody());
        assertEquals("My First Post", getPostResponse.getBody().title());
        assertEquals("This is the content of my first blog post.", getPostResponse.getBody().content());
        assertEquals("flowuser", getPostResponse.getBody().author().username());
        assertTrue(getPostResponse.getBody().comments().isEmpty());

        // 5. Add a comment to the post
        CommentRequest commentRequest = new CommentRequest("Great post! Very informative.");
        ResponseEntity<CommentResponse> commentResponse = restTemplate.exchange(
                baseUrl + "/api/v1/posts/" + postId + "/comments", HttpMethod.POST,
                new HttpEntity<>(commentRequest, authHeaders), CommentResponse.class);

        assertEquals(HttpStatus.CREATED, commentResponse.getStatusCode());
        assertNotNull(commentResponse.getBody());
        assertEquals("Great post! Very informative.", commentResponse.getBody().text());
        assertEquals("flowuser", commentResponse.getBody().author().username());

        // 6. Read comments (public endpoint)
        ResponseEntity<String> commentsResponse = restTemplate.getForEntity(
                baseUrl + "/api/v1/posts/" + postId + "/comments", String.class);

        assertEquals(HttpStatus.OK, commentsResponse.getStatusCode());
        assertNotNull(commentsResponse.getBody());
        assertTrue(commentsResponse.getBody().contains("Great post! Very informative."));
    }

    @Test
    void register_withDuplicateUsername_shouldReturn409() {
        UserRegisterRequest request = new UserRegisterRequest("duplicateuser", "pass1234");
        restTemplate.postForEntity(baseUrl + "/api/v1/users", request, UserResponse.class);

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class,
                () -> restTemplate.postForEntity(baseUrl + "/api/v1/users", request, String.class));

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
    }

    @Test
    void createPost_withoutToken_shouldReturn401() {
        PostCreateRequest postRequest = new PostCreateRequest("Unauthorized Post", "Content");

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class,
                () -> restTemplate.postForEntity(baseUrl + "/api/v1/posts", postRequest, String.class));

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
    }

    @Test
    void login_withInvalidCredentials_shouldReturn401() {
        AuthRequest authRequest = new AuthRequest("nonexistentuser", "wrongpassword");

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class,
                () -> restTemplate.postForEntity(baseUrl + "/api/v1/auth", authRequest, String.class));

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
    }

    @Test
    void getPost_notFound_shouldReturn404() {
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class,
                () -> restTemplate.getForEntity(baseUrl + "/api/v1/posts/99999", String.class));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }
}
