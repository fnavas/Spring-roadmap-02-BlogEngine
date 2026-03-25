package com.fnavas.blogengine.service;

import com.fnavas.blogengine.dto.request.PostCreateRequest;
import com.fnavas.blogengine.dto.response.PostDetailResponse;
import com.fnavas.blogengine.dto.response.PostResponse;

import java.util.List;

public interface PostService {
    List<PostResponse> getAllPosts(String author, String title);
    PostDetailResponse getPostById(Long id);
    PostResponse createPost(PostCreateRequest postRequest);
    PostResponse updatePost(Long id, PostCreateRequest postRequest);
    void deletePost(Long id);
}
