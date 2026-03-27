package com.fnavas.blogengine.service;

import com.fnavas.blogengine.entity.Role;
import com.fnavas.blogengine.entity.User;
import com.fnavas.blogengine.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailService customUserDetailService;

    @Test
    void loadUserByUsername_existingUser_returnsUserDetails() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("encodedpassword");
        user.setRole(Role.ROLE_USER);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        UserDetails result = customUserDetailService.loadUserByUsername("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    void loadUserByUsername_unknownUser_throwsUsernameNotFoundException() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        UsernameNotFoundException ex = assertThrows(
                UsernameNotFoundException.class,
                () -> customUserDetailService.loadUserByUsername("ghost"));

        assertEquals("User not found: ghost", ex.getMessage());
        verify(userRepository, times(1)).findByUsername("ghost");
    }
}
