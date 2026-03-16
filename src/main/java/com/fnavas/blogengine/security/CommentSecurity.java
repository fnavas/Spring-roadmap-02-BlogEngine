package com.fnavas.blogengine.security;

import com.fnavas.blogengine.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentSecurity {

    private final CommentRepository commentRepository;

    public boolean isAuthor(Long commentId, String username) {
        log.info("[isAuthor]-Checking if user is comment author");
        log.debug("[isAuthor]-commentId: {}, username: {}", commentId, username);
        return commentRepository.findById(commentId)
                .map(comment -> comment.getAuthor().getUsername().equals(username))
                .orElse(false);
    }
}
