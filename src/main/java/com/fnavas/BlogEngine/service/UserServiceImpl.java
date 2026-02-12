package com.fnavas.BlogEngine.service;

import com.fnavas.BlogEngine.dto.UserRegisterRequest;
import com.fnavas.BlogEngine.dto.UserResponse;
import com.fnavas.BlogEngine.entity.Role;
import com.fnavas.BlogEngine.entity.User;
import com.fnavas.BlogEngine.exception.UserNotFoundException;
import com.fnavas.BlogEngine.exception.UserWithUsernameException;
import com.fnavas.BlogEngine.mapper.UserMapper;
import com.fnavas.BlogEngine.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

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
}
