package com.fnavas.BlogEngine.service;

import com.fnavas.BlogEngine.dto.PostCreateRequest;
import com.fnavas.BlogEngine.dto.PostResponse;
import com.fnavas.BlogEngine.entity.Post;
import com.fnavas.BlogEngine.entity.Role;
import com.fnavas.BlogEngine.entity.User;
import com.fnavas.BlogEngine.exception.PostNotFoundException;
import com.fnavas.BlogEngine.mapper.PostMapper;
import com.fnavas.BlogEngine.repository.PostRepository;
import com.fnavas.BlogEngine.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostMapper postMapper;

    public PostServiceImpl(PostRepository postRepository, UserRepository userRepository , PostMapper postMapper) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
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
        log.debug("[getPostById]-Service get post by id: {}", post);
        return postMapper.toResponse(post);
    }

    @Override
    public PostResponse createPost(PostCreateRequest postRequest) {
        log.info("[createPost]-Service request to create post");
        log.debug("[createPost]-Service request to create post: {}", postRequest);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        log.debug("[createPost]-Service username : {}", username);
        User author = userRepository.findByUsername(username)
               .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
        log.debug("[createPost]-Service author : {}", author);
        Post post = postMapper.toEntity(postRequest);
        post.setAuthor(author);
        Post savedPost = postRepository.save(post);
        log.info("[createPost]-Service post created successfully");
        log.debug("[createPost]-Service post created successfully with id: {}", savedPost.getId());
        return postMapper.toResponse(savedPost);
    }

    @Override
    public PostResponse updatePost(Long id, PostCreateRequest postRequest) {
        log.info("[updatePost]-Service request to update post");
        log.debug("[updatePost]-Service request to update post: {}, {}", id, postRequest);
        Post post = postRepository.findById(id).orElseThrow(
                () -> new PostNotFoundException("Post not found with id: " + id)
        );
        log.debug("[updatePost]-Service post to update: {}", post);
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ADMIN"));
        boolean isAuthor = post.getAuthor().getUsername().equals(SecurityContextHolder.getContext()
                .getAuthentication().getName());
        if (!isAdmin && !isAuthor) {
            log.warn("[updatePost]-Service unauthorized attempt to update post with id: {}", id);
            throw new RuntimeException("Unauthorized to update this post");
        }
        post.setTitle(postRequest.title());
        post.setContent(postRequest.content());
        Post updatedPost = postRepository.save(post);
        log.info("[updatePost]-Service post updated successfully");
        log.debug("[updatePost]-Service post updated successfully with id: {}", updatedPost.getId());
        return postMapper.toResponse(updatedPost);
    }
}
