package com.fnavas.blogengine.service;

import com.fnavas.blogengine.dto.request.UserRegisterRequest;
import com.fnavas.blogengine.dto.response.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse createUser(UserRegisterRequest userRequest);

    List<UserResponse> getAllUsers();

    UserResponse getUserById(Long id);

    UserResponse getUserByUsername(String username);

    UserResponse updateUser(Long id, UserRegisterRequest userRequest);

    void deleteUser(Long id);
}
