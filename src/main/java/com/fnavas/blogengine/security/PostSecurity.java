package com.fnavas.blogengine.security;

import com.fnavas.blogengine.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostSecurity {

    private final PostRepository postRepository;

    public boolean isAuthor(Long id, String username) {
        log.info("[isAuthor]-Checking if author exists for user");
        log.debug("[isAuthor]-Checking if author exists for user with id: {} and username: {}", id, username);
        return postRepository.findById(id).map(
                post -> post.getAuthor().getUsername().equals(username)).orElse(false);
    }
}
