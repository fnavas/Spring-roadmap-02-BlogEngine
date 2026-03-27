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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder encoder;

    @Override
    @Transactional
    public UserResponse createUser(UserRegisterRequest userRequest) {
        log.info("[createUser]-Service creating new user with username: {}", userRequest.username());
        if (userRepository.findByUsername(userRequest.username()).isPresent()) {
            log.warn("[createUser]-Service user with username {} already exists", userRequest.username());
            throw new UserWithUsernameException("User with username " + userRequest.username() + " already exists");
        }
        User newUser = userMapper.toEntity(userRequest);
        newUser.setRole(Role.ROLE_USER);
        newUser.setPassword(encoder.encode(userRequest.password()));
        User savedUser = userRepository.save(newUser);
        log.info("[createUser]-Service user created successfully");
        log.debug("[createUser]-Service user created with id: {}", savedUser.getId());
        return userMapper.toResponse(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        log.info("[getAllUsers]-Service request to get all users");
        Page<User> users = userRepository.findAll(pageable);
        log.info("[getAllUsers]-Service users retrieved successfully");
        return users.map(userMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public Page<UserResponse> searchByUsername(String username, Pageable pageable) {
        log.info("[searchByUsername]-Service request to search users by username: {}", username);
        Page<User> users = userRepository.findByUsernameContainingIgnoreCase(username, pageable);
        log.info("[searchByUsername]-Service found {} users matching '{}'", users.getTotalElements(), username);
        return users.map(userMapper::toResponse);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and @userSecurity.isUser(#id, authentication.name))")
    public UserResponse updateUser(Long id, UserRegisterRequest userRequest) {
        log.info("[updateUser]-Service request to update user");
        log.debug("[updateUser]-Service request to update user with username: {}", userRequest.username());
        User user = userRepository.findById(id) .orElseThrow(() -> {
            log.warn("[updateUser]-Service user with username {} not found", userRequest.username());
            return new UserNotFoundException("User with username " + userRequest.username() + " not found");
        });
        if (!userRequest.username().equals(user.getUsername())
                && userRepository.findByUsername(userRequest.username()).isPresent()) {
            log.warn("[updateUser]-Service user with username {} already exists", userRequest.username());
            throw new UserWithUsernameException("User with username " + userRequest.username() + " already exists");
        }
        user.setUsername(userRequest.username());
        user.setPassword(encoder.encode(userRequest.password()));
        User updatedUser = userRepository.save(user);
        log.info("[updateUser]-Service user updated successfully");
        log.debug("[updateUser]-Service user updated with id: {}", updatedUser.getId());
        return userMapper.toResponse(updatedUser);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and @userSecurity.isUser(#id, authentication.name))")
    public void deleteUser(Long id) {
        log.info("[deleteUser]-Service request to delete user with id: {}", id);
        User user = userRepository.findById(id).orElseThrow(() -> {
            log.warn("[deleteUser]-Service user with id {} not found", id);
            return new UserNotFoundException("User with id " + id + " not found");
        });
        userRepository.delete(user);
        log.info("[deleteUser]-Service user deleted successfully");
    }
}
