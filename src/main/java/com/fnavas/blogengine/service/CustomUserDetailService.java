package com.fnavas.blogengine.service;

import com.fnavas.blogengine.entity.User;
import com.fnavas.blogengine.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("[loadUserByUsername]-Service request to load user by username");
        log.debug("[loadUserByUsername]-Service request to load user by username: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
        log.debug("[loadUserByUsername]-User name found {}", user.getUsername());
        log.debug("[loadUserByUsername]-Password in DB   : {}", user.getPassword());
        log.debug("[loadUserByUsername]-Role in DB {}", user.getRole());
        return user;
    }
}
