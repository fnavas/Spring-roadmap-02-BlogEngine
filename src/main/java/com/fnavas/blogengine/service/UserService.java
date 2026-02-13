package com.fnavas.blogengine.service;

import com.fnavas.blogengine.dto.UserRegisterRequest;
import com.fnavas.blogengine.dto.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse createUser(UserRegisterRequest userRequest);

    List<UserResponse> getAllUsers();

    UserResponse getUserById(Long id);

    UserResponse getUserByUsername(String username);

    UserResponse updateUser(UserRegisterRequest userRequest);
}
