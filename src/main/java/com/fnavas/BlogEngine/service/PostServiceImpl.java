package com.fnavas.BlogEngine.service;

import com.fnavas.BlogEngine.dto.PostResponse;
import com.fnavas.BlogEngine.entity.Post;
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
        return posts.stream()
                .map(postMapper::toResponse)
                .toList();

    }
}
