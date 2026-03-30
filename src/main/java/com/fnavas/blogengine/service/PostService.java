package com.fnavas.blogengine.service;

import com.fnavas.blogengine.dto.request.PostCreateRequest;
import com.fnavas.blogengine.dto.request.PostFilter;
import com.fnavas.blogengine.dto.response.PostDetailResponse;
import com.fnavas.blogengine.dto.response.PostResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostService {
    Page<PostResponse> getAllPosts(PostFilter filter, Pageable pageable);
    PostDetailResponse getPostById(Long id);
    PostResponse createPost(PostCreateRequest postRequest);
    PostResponse updatePost(Long id, PostCreateRequest postRequest);
    void deletePost(Long id);
}

