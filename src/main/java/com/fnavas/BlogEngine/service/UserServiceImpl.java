package com.fnavas.BlogEngine.service;

import com.fnavas.BlogEngine.dto.UserRegisterRequest;
import com.fnavas.BlogEngine.dto.UserResponse;
import com.fnavas.BlogEngine.entity.Role;
import com.fnavas.BlogEngine.entity.User;
import com.fnavas.BlogEngine.exception.UserWithUsernameException;
import com.fnavas.BlogEngine.mapper.UserMapper;
import com.fnavas.BlogEngine.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
}
