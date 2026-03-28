package com.fnavas.blogengine.api;

import com.fnavas.blogengine.dto.request.UserRegisterRequest;
import com.fnavas.blogengine.dto.response.UserResponse;
import com.fnavas.blogengine.exception.ErrorResponse;
import com.fnavas.blogengine.service.UserService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/users")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Users", description = "User registration and management endpoints")
public class UserRestController {

    private final UserService userService;

    @Operation(
            summary = "List all users",
            description = "Returns a paginated list of users. Supports optional search by username (case-insensitive partial match)"
    )
    @ApiResponse(responseCode = "200", description = "Users retrieved successfully")
    @SecurityRequirements
    @GetMapping
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @Parameter(description = "Search by username (partial match)", example = "fnavas")
            @RequestParam(required = false) String username,
            @PageableDefault(size = 10, sort = "username") Pageable pageable) {
        log.info("[getAllUsers]-RestController request to get all users");
        if (username != null && !username.isBlank()) {
            log.info("[getAllUsers]-RestController request to search users by username: {}", username);
            return ResponseEntity.ok(userService.searchByUsername(username, pageable));
        }
        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

    @Operation(
            summary = "Get user by ID",
            description = "Returns a single user's public profile"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirements
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(
            @Parameter(description = "User ID", example = "1") @PathVariable Long id) {
        log.info("[getUserById]-RestController request to get user by id: {}", id);
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account with ROLE_USER. No authentication required"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User registered successfully",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Username already exists",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirements
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRegisterRequest userRequest) {
        log.info("[createUser]-RestController request to create a new user with username: {}", userRequest.username());
        UserResponse createdUser = userService.createUser(userRequest);
        URI location = URI.create(String.format("/api/v1/users/%s", createdUser.id()));
        return ResponseEntity.created(location).body(createdUser);
    }

    @Operation(
            summary = "Update a user",
            description = "Updates user credentials. Only the account owner or an admin can perform this action"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated successfully",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Authentication required",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Not the account owner",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Username already taken",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<UserResponse> updateUser(
            @Parameter(description = "User ID", example = "1") @PathVariable Long id,
            @Valid @RequestBody UserRegisterRequest userRequest) {
        log.info("[updateUser]-RestController request to update user with id: {}", id);
        UserResponse updatedUser = userService.updateUser(id, userRequest);
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(
            summary = "Delete a user",
            description = "Deletes a user account. Only the account owner or an admin can perform this action"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Not the account owner",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "User ID", example = "1") @PathVariable Long id) {
        log.info("[deleteUser]-RestController request to delete user with id: {}", id);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
