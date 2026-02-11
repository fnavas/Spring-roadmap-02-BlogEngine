package com.fnavas.BlogEngine.service;

import com.fnavas.BlogEngine.dto.UserRegisterRequest;
import com.fnavas.BlogEngine.dto.UserResponse;
import com.fnavas.BlogEngine.entity.Role;
import com.fnavas.BlogEngine.entity.User;
import com.fnavas.BlogEngine.exception.UserWithUsernameException;
import com.fnavas.BlogEngine.mapper.UserMapper;
import com.fnavas.BlogEngine.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUser_ok_shouldReturnUserReponse() {
        UserRegisterRequest userRequest = new UserRegisterRequest("testuser", "password");
        when(userRepository.save(any(User.class))).thenReturn(new User());
        when(userRepository.findByUsername("testuser")).thenReturn(java.util.Optional.empty());
        when(userMapper.toEntity(userRequest)).thenReturn(new User());
        when(userMapper.toResponse(any(User.class))).thenReturn(new UserResponse(1L, "testuser", Role.ROLE_USER));

        UserResponse userResponse = userService.createUser(userRequest);

        assertNotNull(userResponse);
        assertEquals("testuser", userResponse.username());
        verify(userRepository, times(1)).save(any(User.class));
        verify(userMapper, times(1)).toEntity(userRequest);
        verify(userMapper, times(1)).toResponse(any(User.class));
        verify(userRepository, times(1)).findByUsername("testuser");

    }

    @Test
    void createUser_userAlreadyExists_shouldThrowException() {
        UserRegisterRequest userRequest = new UserRegisterRequest("testuser", "password");
        when(userRepository.findByUsername("testuser")).thenReturn(java.util.Optional.of(new User()));

        UserWithUsernameException ex = assertThrows(
                UserWithUsernameException.class, () -> userService.createUser(userRequest));

        assertEquals("User with username testuser already exists", ex.getMessage());
        verify(userRepository, times(1)).findByUsername("testuser");
        verify(userRepository, never()).save(any(User.class));
        verify(userMapper, never()).toEntity(any(UserRegisterRequest.class));
        verify(userMapper, never()).toResponse(any(User.class));
    }
}