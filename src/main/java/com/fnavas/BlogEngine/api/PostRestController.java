package com.fnavas.BlogEngine.api;

import com.fnavas.BlogEngine.dto.PostResponse;
import com.fnavas.BlogEngine.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
@Slf4j
public class PostRestController {

    private final PostService postService;

    public PostRestController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public ResponseEntity<List<PostResponse>> getAllPosts() {
        log.info("[getAllPosts]-RestController request to get all posts");
        return ResponseEntity.ok().body(postService.getAllPosts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable Long id) {
        log.info("[getPostById]-RestController request to get post by id");
        log.debug("[getPostById]-RestController request to get post by id: {}", id);
        return ResponseEntity.ok().body(postService.getPostById(id));
    }
}
