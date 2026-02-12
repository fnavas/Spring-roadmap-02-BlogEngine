package com.fnavas.BlogEngine.service;

import com.fnavas.BlogEngine.dto.PostCreateRequest;
import com.fnavas.BlogEngine.dto.PostResponse;
import com.fnavas.BlogEngine.entity.Post;
import com.fnavas.BlogEngine.entity.User;
import com.fnavas.BlogEngine.exception.PostNotFoundException;
import com.fnavas.BlogEngine.exception.UserNotFoundException;
import com.fnavas.BlogEngine.mapper.PostMapper;
import com.fnavas.BlogEngine.repository.PostRepository;
import com.fnavas.BlogEngine.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostMapper postMapper;

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
    public List<PostResponse> getPostsByAuthor(String username) {
        log.info("[getPostsByAuthor]-Service request to get posts by author");
        log.debug("[getPostsByAuthor]-Service request to get posts by author: {}", username);
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
        log.debug("[getPostsByAuthor]-Service get author: {}", author);
        List<Post> posts = postRepository.findByAuthor(author);
        log.debug("[getPostsByAuthor]-Service get posts by author: {}", posts);
        return posts.stream()
                .map(postMapper::toResponse)
                .toList();
    }

    @Override
    public List<PostResponse> getPostsByTitle(String title) {
        log.info("[getPostsByTitle]-Service request to get posts by title");
        log.debug("[getPostsByTitle]-Service request to get posts by title: {}", title);
        List<Post> posts = postRepository.findByTitleContainingIgnoreCase(title);
        log.debug("[getPostsByTitle]-Service get posts by title: {}", posts);
        return posts.stream()
                .map(postMapper::toResponse)
                .toList();
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public PostResponse createPost(PostCreateRequest postRequest) {
        log.info("[createPost]-Service request to create post");
        log.debug("[createPost]-Service request to create post: {}", postRequest);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        log.debug("[createPost]-Service username : {}", username);
        User author = userRepository.findByUsername(username)
               .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
        log.debug("[createPost]-Service author : {}", author);
        Post post = postMapper.toEntity(postRequest);
        post.setAuthor(author);
        Post savedPost = postRepository.save(post);
        log.info("[createPost]-Service post created successfully");
        log.debug("[createPost]-Service post created successfully with id: {}", savedPost.getId());
        return postMapper.toResponse(savedPost);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN') or @postSecurity.isAuthor(#id, authentication.name)")
    public PostResponse updatePost(Long id, PostCreateRequest postRequest) {
        log.info("[updatePost]-Service request to update post");
        log.debug("[updatePost]-Service request to update post: {}, {}", id, postRequest);
        Post post = postRepository.findById(id).orElseThrow(
                () -> new PostNotFoundException("Post not found with id: " + id)
        );
        post.setTitle(postRequest.title());
        post.setContent(postRequest.content());
        Post updatedPost = postRepository.save(post);
        log.info("[updatePost]-Service post updated successfully");
        log.debug("[updatePost]-Service post updated successfully with id: {}", updatedPost.getId());
        return postMapper.toResponse(updatedPost);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN') or @postSecurity.isAuthor(#id, authentication.name)")
    public void deletePost(Long id) {
        log.info("[deletePost]-Service request to delete post");
        log.debug("[deletePost]-Service request to delete post: {}", id);
        Post post = postRepository.findById(id).orElseThrow(
                () -> new PostNotFoundException("Post not found with id: " + id)
        );
        postRepository.delete(post);
        log.info("[deletePost]-Service post deleted successfully");
    }
}
