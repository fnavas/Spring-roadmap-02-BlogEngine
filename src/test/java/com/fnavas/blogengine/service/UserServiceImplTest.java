package com.fnavas.blogengine.service;

import com.fnavas.blogengine.dto.request.UserRegisterRequest;
import com.fnavas.blogengine.dto.response.UserResponse;
import com.fnavas.blogengine.entity.Role;
import com.fnavas.blogengine.entity.User;
import com.fnavas.blogengine.exception.UserNotFoundException;
import com.fnavas.blogengine.exception.UserWithUsernameException;
import com.fnavas.blogengine.mapper.UserMapper;
import com.fnavas.blogengine.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder encoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void getAllUsers_ok_shouldReturnListOfUserResponses() {
        when(userRepository.findAll()).thenReturn(java.util.List.of(new User()));
        when(userMapper.toResponse(any(User.class))).thenReturn(new UserResponse(1L, "testuser", Role.ROLE_USER));

        List<UserResponse> users = userService.getAllUsers();

        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals("testuser", users.get(0).username());
        verify(userRepository, times(1)).findAll();
        verify(userMapper, times(1)).toResponse(any(User.class));
    }

    @Test
void getUserById_ok_shouldReturnUserResponse() {
        Long id = 1L;
        when(userRepository.findById(id)).thenReturn(Optional.of(new User()));
        when(userMapper.toResponse(any(User.class))).thenReturn(new UserResponse(id, "testuser", Role.ROLE_USER));

        UserResponse userResponse = userService.getUserById(id);

        assertNotNull(userResponse); assertEquals("testuser", userResponse.username());
        verify(userRepository, times(1)).findById(id);
        verify(userMapper, times(1)).toResponse(any(User.class));
    }

    @Test
void getUserById_userNotFound_shouldThrowException() {
        Long id = 1L;
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(UserNotFoundException.class, () -> userService.getUserById(1L));

        assertEquals("User with id "+id+" not found", ex.getMessage());
        verify(userRepository, times(1)).findById(id);
        verify(userMapper, never()).toResponse(any(User.class));
    }

    @Test
void getUserByUsername_ok_shouldReturnUserResponse() {
        String username = "testuser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(new User()));
        when(userMapper.toResponse(any(User.class))).thenReturn(new UserResponse(1L, username, Role.ROLE_USER));

        UserResponse userResponse = userService.getUserByUsername(username);

        assertNotNull(userResponse);
        assertEquals(username, userResponse.username());
        verify(userRepository, times(1)).findByUsername(username);
        verify(userMapper, times(1)).toResponse(any(User.class));
    }
    @Test
    void getUserByUsername_userNotFound_shouldThrowException() {
        String username = "testuser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(UserNotFoundException.class, () -> userService.getUserByUsername(username));

        assertEquals("User with username "+username+" not found", ex.getMessage());
        verify(userRepository, times(1)).findByUsername(username);
        verify(userMapper, never()).toResponse(any(User.class)); }

    @Test
    void createUser_ok_shouldReturnUserReponse() {
        UserRegisterRequest userRequest = new UserRegisterRequest("testuser", "password");
        when(userRepository.save(any(User.class))).thenReturn(new User());
        when(userRepository.findByUsername("testuser")).thenReturn(java.util.Optional.empty());
        when(userMapper.toEntity(userRequest)).thenReturn(new User());
        when(userMapper.toResponse(any(User.class))).thenReturn(new UserResponse(1L, "testuser", Role.ROLE_USER));
        when(encoder.encode(userRequest.password())).thenReturn("password");

        UserResponse userResponse = userService.createUser(userRequest);

        assertNotNull(userResponse);
        assertEquals("testuser", userResponse.username());
        verify(userRepository, times(1)).save(any(User.class));
        verify(userMapper, times(1)).toEntity(userRequest);
        verify(userMapper, times(1)).toResponse(any(User.class));
        verify(userRepository, times(1)).findByUsername("testuser");
        verify(encoder, times(1)).encode(userRequest.password());

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