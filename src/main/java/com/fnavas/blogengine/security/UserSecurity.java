package com.fnavas.blogengine.security;

import com.fnavas.blogengine.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserSecurity {

    private final UserRepository userRepository;

    public boolean isUser(Long id, String username) {
        log.info("[isUser]-Checking if user exists for user");
        log.debug("[isUser]-Checking if user exists for user with id: {} and username: {}", id, username);
        return userRepository.findById(id).map(
                user -> user.getUsername().equals(username)).orElse(false);
    }
}
