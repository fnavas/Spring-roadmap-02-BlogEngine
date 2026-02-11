package com.fnavas.BlogEngine.service;

import com.fnavas.BlogEngine.dto.UserRegisterRequest;
import com.fnavas.BlogEngine.dto.UserResponse;

public interface UserService {
    UserResponse createUser(UserRegisterRequest userRequest);
}
