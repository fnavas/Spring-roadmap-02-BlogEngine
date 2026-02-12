package com.fnavas.blogengine.service;

import com.fnavas.blogengine.dto.PostCreateRequest;
import com.fnavas.blogengine.dto.PostResponse;

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
