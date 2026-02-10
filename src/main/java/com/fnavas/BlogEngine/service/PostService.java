package com.fnavas.BlogEngine.service;

import com.fnavas.BlogEngine.dto.PostCreateRequest;
import com.fnavas.BlogEngine.dto.PostResponse;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface PostService {
    public List<PostResponse> getAllPosts();
    public PostResponse getPostById(Long id);
    public PostResponse createPost(PostCreateRequest postRequest);
    public PostResponse updatePost(Long id, PostCreateRequest postRequest);

    @PreAuthorize("hasRole('ADMIN') or @postSecurity.isAuthor(#id, authentication.name)")
    void deletePost(Long id);
}
