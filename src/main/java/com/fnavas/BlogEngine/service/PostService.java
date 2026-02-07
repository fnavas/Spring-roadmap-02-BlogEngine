package com.fnavas.BlogEngine.service;

import com.fnavas.BlogEngine.dto.PostCreateRequest;
import com.fnavas.BlogEngine.dto.PostResponse;

import java.util.List;

public interface PostService {
    public List<PostResponse> getAllPosts();
    public PostResponse getPostById(Long id);
    public PostResponse createPost(PostCreateRequest postRequest);
    public PostResponse updatePost(Long id, PostCreateRequest postRequest);
}
