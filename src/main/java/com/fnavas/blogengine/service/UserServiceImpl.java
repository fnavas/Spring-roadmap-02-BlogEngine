package com.fnavas.blogengine.service;

import com.fnavas.blogengine.dto.request.UserRegisterRequest;
import com.fnavas.blogengine.dto.response.UserResponse;
import com.fnavas.blogengine.entity.Role;
import com.fnavas.blogengine.entity.User;
import com.fnavas.blogengine.exception.UserNotFoundException;
import com.fnavas.blogengine.exception.UserWithUsernameException;
import com.fnavas.blogengine.mapper.UserMapper;
import com.fnavas.blogengine.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder encoder;

    @Override
    public UserResponse createUser(UserRegisterRequest userRequest) {
        log.info("[createUser]-Service creating new user");
        log.debug("[createUser]-Service creating new user with username:" +
                " {} password:{}", userRequest.username(), userRequest.password());
        if (userRepository.findByUsername(userRequest.username()).isPresent()) {
            log.warn("[createUser]-Service user with username {} already exists", userRequest.username());
            throw new UserWithUsernameException("User with username " + userRequest.username() + " already exists");
        }
        User newUser = userMapper.toEntity(userRequest);
        newUser.setRole(Role.ROLE_USER);
        //TODO: enhance password encoding
        newUser.setPassword(encoder.encode(userRequest.password()));
        User savedUser = userRepository.save(newUser);
        log.info("[createUser]-Service user created successfully");
        log.debug("[createUser]-Service user created with id: {}", savedUser.getId());
        return userMapper.toResponse(savedUser);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        log.info("[getAllUsers]-Service request to get all users");
        List<User> users = userRepository.findAll();
        log.info("[getAllUsers]-Service users retrieved successfully");
        return users.stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @Override
    public UserResponse getUserById(Long id) {
        log.info("[getUserById]-Service request to get user by id: {}", id);
        User user = userRepository.findById(id) .orElseThrow(() -> {
            log.warn("[getUserById]-Service user with id {} not found", id);
            return new UserNotFoundException("User with id " + id + " not found");
        });
        log.info("[getUserById]-Service user retrieved successfully");
        log.debug("[getUserById]-Service user retrieved with username: {}", user.getUsername());
        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse getUserByUsername(String username) {
        log.info("[getUserByUsername]-Service request to get user by username");
        log.debug("[getUserByUsername]-Service request to get user by username: {}", username);
        User user = userRepository.findByUsername(username) .orElseThrow(() -> {
            log.warn("[getUserByUsername]-Service user with username {} not found", username);
            return new UserNotFoundException("User with username " + username + " not found"); });
        log.info("[getUserByUsername]-Service user retrieved successfully");
        log.debug("[getUserByUsername]-Service user retrieved with id: {}", user.getId());
        return userMapper.toResponse(user);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and authentication.name == #userRequest.username())")
    public UserResponse updateUser(UserRegisterRequest userRequest) {
        log.info("[updateUser]-Service request to update user");
        log.debug("[updateUser]-Service request to update user with username: {}", userRequest.username());
        User user = userRepository.findByUsername(userRequest.username()) .orElseThrow(() -> {
            log.warn("[updateUser]-Service user with username {} not found", userRequest.username());
            return new UserNotFoundException("User with username " + userRequest.username() + " not found");
        });
        //TODO: change username
        user.setPassword(encoder.encode(userRequest.password()));
        user.setUpdatedAt(LocalDateTime.now());
        User updatedUser = userRepository.save(user);
        log.info("[updateUser]-Service user updated successfully");
        log.debug("[updateUser]-Service user updated with id: {}", updatedUser.getId());
        return userMapper.toResponse(updatedUser);
    }
}
