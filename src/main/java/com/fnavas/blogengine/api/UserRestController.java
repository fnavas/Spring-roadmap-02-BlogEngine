package com.fnavas.blogengine.api;

import com.fnavas.blogengine.dto.UserRegisterRequest;
import com.fnavas.blogengine.dto.UserResponse;
import com.fnavas.blogengine.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@Slf4j
@RequiredArgsConstructor
public class UserRestController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        log.info("[getAllUsers]-RestController request to get all users");
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        log.info("[getUserById]-RestController request to get user by id: {}", id);
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/username/{username}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<UserResponse> getUserByUsername(@PathVariable String username) {
        log.info("[getUserByUsername]-RestController request to get user by username: {}", username);
        UserResponse user = userService.getUserByUsername(username);
        return ResponseEntity.ok(user);
    }

    @PostMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<UserResponse> createUser(@RequestBody UserRegisterRequest userRequest) {
        log.info("[createUser]-RestController request to create a new user");
        log.debug("[createUser]-RestController request to create a new user with username: {}" +
                " password: {}", userRequest.username(), userRequest.password());
        UserResponse createdUser = userService.createUser(userRequest);
        URI location = URI.create(String.format("/api/v1/users/%s", createdUser.id()));
        return ResponseEntity.created(location).body(createdUser);
    }

    @PutMapping()
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<UserResponse> updateUser(@RequestBody UserRegisterRequest userRequest) {
        log.info("[updateUser]-RestController request to update user");
        log.debug("[updateUser]-RestController request to update user with" +
                " username: {} password: {}", userRequest.username(), userRequest.password());
        UserResponse updatedUser = userService.updateUser(userRequest); return ResponseEntity.ok(updatedUser); }
}
