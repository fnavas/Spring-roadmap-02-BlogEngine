package com.fnavas.blogengine.api;

import com.fnavas.blogengine.dto.request.PostCreateRequest;
import com.fnavas.blogengine.dto.request.PostFilter;
import com.fnavas.blogengine.dto.response.PostDetailResponse;
import com.fnavas.blogengine.dto.response.PostResponse;
import com.fnavas.blogengine.exception.ErrorResponse;
import com.fnavas.blogengine.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/posts")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Posts", description = "Blog post management endpoints")
public class PostRestController {

    private final PostService postService;

    @Operation(
            summary = "List all posts",
            description = "Returns a paginated list of posts. Supports optional filtering by author and title (case-insensitive partial match)"
    )
    @ApiResponse(responseCode = "200", description = "Posts retrieved successfully")
    @SecurityRequirements
    @GetMapping
    public ResponseEntity<Page<PostResponse>> getAllPosts(
            @ModelAttribute PostFilter filter,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("[getAllPosts]-RestController request to get all posts");
        log.debug("[getAllPosts]-RestController request to get all posts with filter: {}", filter);
        return ResponseEntity.ok().body(postService.getAllPosts(filter, pageable));
    }

    @Operation(
            summary = "Get post by ID",
            description = "Returns a single post with its full content and comments"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Post found",
                    content = @Content(schema = @Schema(implementation = PostDetailResponse.class))),
            @ApiResponse(responseCode = "404", description = "Post not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirements
    @GetMapping("/{id}")
    public ResponseEntity<PostDetailResponse> getPostById(
            @Parameter(description = "Post ID", example = "1") @PathVariable Long id) {
        log.info("[getPostById]-RestController request to get post by id");
        log.debug("[getPostById]-RestController request to get post by id: {}", id);
        return ResponseEntity.ok().body(postService.getPostById(id));
    }

    @Operation(
            summary = "Create a new post",
            description = "Creates a new blog post. The authenticated user is automatically set as the author"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Post created successfully",
                    content = @Content(schema = @Schema(implementation = PostResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Authentication required",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<PostResponse> createPost(@Valid @RequestBody PostCreateRequest postRequest) {
        log.info("[createPost]-RestController request to create a new post");
        log.debug("[createPost]-RestController request to create a new post: {}", postRequest);
        PostResponse createdPost = postService.createPost(postRequest);
        URI location = URI.create(String.format("/api/v1/posts/%s", createdPost.id()));
        log.debug("[createPost]-RestController request to create a new post at: {}", location);
        return ResponseEntity.created(location).body(createdPost);
    }

    @Operation(
            summary = "Update a post",
            description = "Updates an existing post. Only the post author or an admin can perform this action"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Post updated successfully",
                    content = @Content(schema = @Schema(implementation = PostResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Authentication required",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Not the post author",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Post not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<PostResponse> updatePost(
            @Parameter(description = "Post ID", example = "1") @PathVariable Long id,
            @Valid @RequestBody PostCreateRequest postRequest) {
        log.info("[updatePost]-RestController request to update post by id");
        log.debug("[updatePost]-RestController request to update post by id: {}, {}", id, postRequest);
        return ResponseEntity.ok().body(postService.updatePost(id, postRequest));
    }

    @Operation(
            summary = "Delete a post",
            description = "Deletes a post and all its comments. Only the post author or an admin can perform this action"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Post deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Not the post author",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Post not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Void> deletePost(
            @Parameter(description = "Post ID", example = "1") @PathVariable Long id) {
        log.info("[deletePost]-RestController request to delete post by id");
        log.debug("[deletePost]-RestController request to delete post by id: {}", id);
        postService.deletePost(id);
        log.info("[deletePost]-RestController request to delete post by id successfully");
        return ResponseEntity.noContent().build();
    }
}
