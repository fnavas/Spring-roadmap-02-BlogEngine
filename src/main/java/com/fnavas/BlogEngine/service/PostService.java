package com.fnavas.BlogEngine.service;

import com.fnavas.BlogEngine.dto.PostCreateRequest;
import com.fnavas.BlogEngine.dto.PostResponse;

import java.util.List;

public interface PostService {
    List<PostResponse> getAllPosts();
    PostResponse getPostById(Long id);

    List<PostResponse> getPostsByAuthor(String username);

    List<PostResponse> getPostsByTitle(String title);

    PostResponse createPost(PostCreateRequest postRequest);
    PostResponse updatePost(Long id, PostCreateRequest postRequest);
    void deletePost(Long id);
}
