package com.fnavas.BlogEngine.api;

import com.fnavas.BlogEngine.dto.PostCreateRequest;
import com.fnavas.BlogEngine.dto.PostResponse;
import com.fnavas.BlogEngine.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
@Slf4j
@RequiredArgsConstructor
public class PostRestController {

    private final PostService postService;

    @GetMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<PostResponse>> getAllPosts() {
        log.info("[getAllPosts]-RestController request to get all posts");
        return ResponseEntity.ok().body(postService.getAllPosts());
    }

    @GetMapping("/{id}")
    @PreAuthorize("permitAll()")
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
        return ResponseEntity.ok().body(postService.createPost(postRequest));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PostResponse> updatePost(@PathVariable Long id, @RequestBody PostCreateRequest postRequest) {
        log.info("[updatePost]-RestController request to update post by id");
        log.debug("[updatePost]-RestController request to update post by id: {}, {}", id, postRequest);
        return ResponseEntity.ok().body(postService.updatePost(id, postRequest));
    }
}
