package com.fnavas.BlogEngine.service;

import com.fnavas.BlogEngine.dto.UserRegisterRequest;
import com.fnavas.BlogEngine.dto.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse createUser(UserRegisterRequest userRequest);

    List<UserResponse> getAllUsers();

    UserResponse getUserById(Long id);

    UserResponse getUserByUsername(String username);
}
