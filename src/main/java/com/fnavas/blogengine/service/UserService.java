package com.fnavas.blogengine.service;

import com.fnavas.blogengine.dto.request.UserRegisterRequest;
import com.fnavas.blogengine.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserResponse createUser(UserRegisterRequest userRequest);

    Page<UserResponse> getAllUsers(Pageable pageable);

    UserResponse getUserById(Long id);

    Page<UserResponse> searchByUsername(String username, Pageable pageable);

    UserResponse updateUser(Long id, UserRegisterRequest userRequest);

    void deleteUser(Long id);
}
