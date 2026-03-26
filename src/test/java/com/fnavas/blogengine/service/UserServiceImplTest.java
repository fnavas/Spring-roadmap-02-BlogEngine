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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
    void getAllUsers_ok_shouldReturnPageOfUserResponses() {
        when(userRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(new User())));
        when(userMapper.toResponse(any(User.class))).thenReturn(new UserResponse(1L, "testuser", Role.ROLE_USER));

        Page<UserResponse> users = userService.getAllUsers(Pageable.unpaged());

        assertNotNull(users);
        assertEquals(1, users.getTotalElements());
        assertEquals("testuser", users.getContent().get(0).username());
        verify(userRepository, times(1)).findAll(any(Pageable.class));
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
    void searchByUsername_ok_shouldReturnMatchingUsers() {
        String username = "test";
        when(userRepository.findByUsernameContainingIgnoreCase(eq(username), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(new User())));
        when(userMapper.toResponse(any(User.class))).thenReturn(new UserResponse(1L, "testuser", Role.ROLE_USER));

        Page<UserResponse> result = userService.searchByUsername(username, Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("testuser", result.getContent().get(0).username());
        verify(userRepository, times(1)).findByUsernameContainingIgnoreCase(eq(username), any(Pageable.class));
        verify(userMapper, times(1)).toResponse(any(User.class));
    }

    @Test
    void searchByUsername_noMatch_shouldReturnEmptyPage() {
        String username = "nobody";
        when(userRepository.findByUsernameContainingIgnoreCase(eq(username), any(Pageable.class)))
                .thenReturn(Page.empty());

        Page<UserResponse> result = userService.searchByUsername(username, Pageable.unpaged());

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).findByUsernameContainingIgnoreCase(eq(username), any(Pageable.class));
        verify(userMapper, never()).toResponse(any(User.class));
    }

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

    @Test
    void updateUser_ok_shouldReturnUserResponse() {
        Long id = 1L;
        UserRegisterRequest request = new UserRegisterRequest("newusername", "newpassword");
        User existingUser = new User();
        existingUser.setUsername("oldusername");

        when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));
        when(encoder.encode(request.password())).thenReturn("encodedpassword");
        when(userRepository.save(any(User.class))).thenReturn(existingUser);
        when(userMapper.toResponse(any(User.class))).thenReturn(new UserResponse(id, "newusername", Role.ROLE_USER));

        UserResponse result = userService.updateUser(id, request);

        assertNotNull(result);
        assertEquals("newusername", result.username());
        verify(userRepository, times(1)).findById(id);
        verify(encoder, times(1)).encode(request.password());
        verify(userRepository, times(1)).save(any(User.class));
        verify(userMapper, times(1)).toResponse(any(User.class));
    }

    @Test
    void updateUser_userNotFound_shouldThrowException() {
        Long id = 1L;
        UserRegisterRequest request = new UserRegisterRequest("newusername", "newpassword");
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        UserNotFoundException ex = assertThrows(UserNotFoundException.class, () -> userService.updateUser(id, request));

        assertEquals("User with username newusername not found", ex.getMessage());
        verify(userRepository, times(1)).findById(id);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUser_ok_shouldDeleteUser() {
        Long id = 1L;
        User user = new User();
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        assertDoesNotThrow(() -> userService.deleteUser(id));

        verify(userRepository, times(1)).findById(id);
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void deleteUser_userNotFound_shouldThrowException() {
        Long id = 1L;
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        UserNotFoundException ex = assertThrows(UserNotFoundException.class, () -> userService.deleteUser(id));

        assertEquals("User with id " + id + " not found", ex.getMessage());
        verify(userRepository, times(1)).findById(id);
        verify(userRepository, never()).delete(any(User.class));
    }
}