package com.fnavas.BlogEngine.repository;

import com.fnavas.BlogEngine.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DataJpaTest
class UserRepositoryIT {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByUsername_whenUserFound_returnUser() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        userRepository.save(user);

        User userFound = userRepository.findByUsername("testuser").orElse(null);

        assertNotNull(userFound);
        assertEquals("testuser", userFound.getUsername());
        assertEquals("password", userFound.getPassword());
    }

    @Test
    void findByUsername_whenUserNotFound_returnNull() {
        User userFound = userRepository.findByUsername("nonexistent").orElse(null);

        assertNull(userFound);
    }
}