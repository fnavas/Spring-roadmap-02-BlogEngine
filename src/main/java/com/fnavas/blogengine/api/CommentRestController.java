package com.fnavas.blogengine.api;

import com.fnavas.blogengine.dto.request.CommentRequest;
import com.fnavas.blogengine.dto.response.CommentResponse;
import com.fnavas.blogengine.exception.ErrorResponse;
import com.fnavas.blogengine.service.CommentService;
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
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts/{postId}/comments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Comments", description = "Comment management endpoints (nested under posts)")
public class CommentRestController {

    private final CommentService commentService;

    @Operation(
            summary = "List comments for a post",
            description = "Returns a paginated list of comments for the specified post"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Comments retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Post not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirements
    @GetMapping
    public ResponseEntity<Page<CommentResponse>> getCommentsByPostId(
            @Parameter(description = "Post ID", example = "1") @PathVariable Long postId,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        log.info("[getCommentsByPostId]-RestController request to get comments for postId: {}", postId);
        return ResponseEntity.ok(commentService.getCommentsByPostId(postId, pageable));
    }

    @Operation(
            summary = "Add a comment to a post",
            description = "Creates a new comment on the specified post. The authenticated user is automatically set as the author"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Comment created successfully",
                    content = @Content(schema = @Schema(implementation = CommentResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Authentication required",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Post not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<CommentResponse> createComment(
            @Parameter(description = "Post ID", example = "1") @PathVariable Long postId,
            @Valid @RequestBody CommentRequest request) {
        log.info("[createComment]-RestController request to create comment for postId: {}", postId);
        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.createComment(postId, request));
    }

    @Operation(
            summary = "Update a comment",
            description = "Updates an existing comment. Only the comment author or an admin can perform this action"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Comment updated successfully",
                    content = @Content(schema = @Schema(implementation = CommentResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Authentication required",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Not the comment author",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Comment not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{commentId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<CommentResponse> updateComment(
            @Parameter(description = "Post ID", example = "1") @PathVariable Long postId,
            @Parameter(description = "Comment ID", example = "1") @PathVariable Long commentId,
            @Valid @RequestBody CommentRequest request) {
        log.info("[updateComment]-RestController request to update commentId: {}", commentId);
        return ResponseEntity.ok(commentService.updateComment(commentId, request));
    }

    @Operation(
            summary = "Delete a comment",
            description = "Deletes a comment. Only the comment author or an admin can perform this action"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Comment deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Not the comment author",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Comment not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{commentId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Void> deleteComment(
            @Parameter(description = "Post ID", example = "1") @PathVariable Long postId,
            @Parameter(description = "Comment ID", example = "1") @PathVariable Long commentId) {
        log.info("[deleteComment]-RestController request to delete commentId: {}", commentId);
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}
