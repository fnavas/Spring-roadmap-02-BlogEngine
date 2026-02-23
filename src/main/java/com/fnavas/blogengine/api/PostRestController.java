package com.fnavas.blogengine.api;

import com.fnavas.blogengine.dto.PostCreateRequest;
import com.fnavas.blogengine.dto.PostResponse;
import com.fnavas.blogengine.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
@Slf4j
@RequiredArgsConstructor
public class PostRestController {

    private final PostService postService;

    @GetMapping
    public ResponseEntity<List<PostResponse>> getAllPosts(
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String title){
        log.info("[getAllPosts]-RestController request to get all posts");
        log.debug("[getAllPosts]-RestController request to get all posts by author: {}", author);
        return ResponseEntity.ok().body(postService.getAllPosts(author, title));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable Long id) {
        log.info("[getPostById]-RestController request to get post by id");
        log.debug("[getPostById]-RestController request to get post by id: {}", id);
        return ResponseEntity.ok().body(postService.getPostById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<PostResponse> createPost(@RequestBody PostCreateRequest postRequest) {
        log.info("[createPost]-RestController request to create a new post");
        log.debug("[createPost]-RestController request to create a new post: {}", postRequest);
        PostResponse createdPost = postService.createPost(postRequest);
        URI location = URI.create(String.format("/api/v1/posts/%s", createdPost.id()));
        log.debug("[createPost]-RestController request to create a new post at: {}", location);
        return ResponseEntity.created(location).body(createdPost);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<PostResponse> updatePost(@PathVariable Long id, @RequestBody PostCreateRequest postRequest) {
        log.info("[updatePost]-RestController request to update post by id");
        log.debug("[updatePost]-RestController request to update post by id: {}, {}", id, postRequest);
        return ResponseEntity.ok().body(postService.updatePost(id, postRequest));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        log.info("[deletePost]-RestController request to delete post by id");
        log.debug("[deletePost]-RestController request to delete post by id: {}", id);
        postService.deletePost(id);
        log.info("[deletePost]-RestController request to delete post by id successfully");
        return ResponseEntity.noContent().build();
    }
}
