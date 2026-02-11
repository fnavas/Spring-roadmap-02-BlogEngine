package com.fnavas.BlogEngine.api;

import com.fnavas.BlogEngine.dto.UserRegisterRequest;
import com.fnavas.BlogEngine.dto.UserResponse;
import com.fnavas.BlogEngine.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/users")
@Slf4j
@RequiredArgsConstructor
public class UserRestController {

    private final UserService userService;

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
}
