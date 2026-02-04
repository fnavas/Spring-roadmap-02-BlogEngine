package com.fnavas.BlogEngine.service;

import com.fnavas.BlogEngine.dto.PostResponse;
import com.fnavas.BlogEngine.entity.Post;
import com.fnavas.BlogEngine.exception.PostNotFoundException;
import com.fnavas.BlogEngine.mapper.PostMapper;
import com.fnavas.BlogEngine.repository.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;

    public PostServiceImpl(PostRepository postRepository, PostMapper postMapper) {
        this.postRepository = postRepository;
        this.postMapper = postMapper;
    }


    @Override
    public List<PostResponse> getAllPosts() {
        log.info("[getAllPosts]-Service request to get all posts");
        List<Post> posts = postRepository.findAll();
        log.debug("[getAllPosts]-Service get all posts: {}", posts);
        return posts.stream()
                .map(postMapper::toResponse)
                .toList();
    }

    @Override
    public PostResponse getPostById(Long id) {
        log.info("[getPostById]-Service request to get post by id");
        log.debug("[getPostById]-Service request to get post by id: {}", id);
        Post post = postRepository.findById(id).orElseThrow(
                () -> new PostNotFoundException("Post not found with id: " + id)
        );
        return postMapper.toResponse(post);
    }
}
