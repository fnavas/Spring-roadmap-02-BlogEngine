package com.fnavas.BlogEngine.exception;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class GlobalHandlerException {

    @ExceptionHandler(value = PostNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePostNotFoundException(PostNotFoundException ex) {
        log.error("Post not found exception: {}", ex.getMessage());
        return ResponseEntity.status(404).body(ErrorResponse.builder()
                        .code(404)
                        .message(ex.getMessage())
                        .error("Post Not Found")
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    @ExceptionHandler(value = UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex) {
        log.error("User not found exception: {}", ex.getMessage());
        return ResponseEntity.status(404).body(ErrorResponse.builder()
                .code(404)
                .message(ex.getMessage())
                .error("User Not Found")
                .timestamp(LocalDateTime.now())
                .build());
    }

    @ExceptionHandler(value = UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(UnauthorizedException ex) {
        log.error("Unauthorized exception: {}", ex.getMessage());
        return ResponseEntity.status(403).body(ErrorResponse.builder()
                .code(403)
                .message(ex.getMessage())
                .error("Unauthorized")
                .timestamp(LocalDateTime.now())
                .build());
    }

    @ExceptionHandler(value = UserWithUsernameException.class)
    public ResponseEntity<ErrorResponse> handleUserWithUsernameException(UserWithUsernameException ex) {
        log.error("User with username exception: {}", ex.getMessage());
        return ResponseEntity.status(400).body(ErrorResponse.builder()
                .code(400)
                .message(ex.getMessage())
                .error("User with username already exists")
                .timestamp(LocalDateTime.now())
                .build());
        }
}
