package com.fnavas.blogengine.security;

import com.fnavas.blogengine.entity.User;
import com.fnavas.blogengine.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserSecurityTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserSecurity userSecurity;

    private User userWithUsername(String username) {
        User user = new User();
        user.setId(1L);
        user.setUsername(username);
        return user;
    }

    @Test
    void isUser_matchingUsername_returnsTrue() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(userWithUsername("testuser")));

        assertTrue(userSecurity.isUser(userId, "testuser"));
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void isUser_differentUsername_returnsFalse() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(userWithUsername("otheruser")));

        assertFalse(userSecurity.isUser(userId, "testuser"));
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void isUser_userNotFound_returnsFalse() {
        Long userId = 99L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertFalse(userSecurity.isUser(userId, "testuser"));
        verify(userRepository, times(1)).findById(userId);
    }
}
