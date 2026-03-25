package com.fnavas.blogengine.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalHandlerException {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException ex) {
        log.warn("Authentication failed: bad credentials");
        return ResponseEntity.status(401).body(ErrorResponse.builder()
                .code(401)
                .message("Invalid username or password")
                .error("Unauthorized")
                .timestamp(LocalDateTime.now())
                .build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("Validation failed: {}", message);
        return ResponseEntity.status(400).body(ErrorResponse.builder()
                .code(400)
                .message(message)
                .error("Validation Failed")
                .timestamp(LocalDateTime.now())
                .build());
    }

    @ExceptionHandler(CommentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCommentNotFoundException(CommentNotFoundException ex) {
        log.error("Comment not found exception: {}", ex.getMessage());
        return ResponseEntity.status(404).body(ErrorResponse.builder()
                .code(404)
                .message(ex.getMessage())
                .error("Comment Not Found")
                .timestamp(LocalDateTime.now())
                .build());
    }

    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePostNotFoundException(PostNotFoundException ex) {
        log.error("Post not found exception: {}", ex.getMessage());
        return ResponseEntity.status(404).body(ErrorResponse.builder()
                .code(404)
                .message(ex.getMessage())
                .error("Post Not Found")
                .timestamp(LocalDateTime.now())
                .build());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex) {
        log.error("User not found exception: {}", ex.getMessage());
        return ResponseEntity.status(404).body(ErrorResponse.builder()
                .code(404)
                .message(ex.getMessage())
                .error("User Not Found")
                .timestamp(LocalDateTime.now())
                .build());
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(UnauthorizedException ex) {
        log.error("Forbidden exception: {}", ex.getMessage());
        return ResponseEntity.status(403).body(ErrorResponse.builder()
                .code(403)
                .message(ex.getMessage())
                .error("Forbidden")
                .timestamp(LocalDateTime.now())
                .build());
    }

    @ExceptionHandler(UserWithUsernameException.class)
    public ResponseEntity<ErrorResponse> handleUserWithUsernameException(UserWithUsernameException ex) {
        log.error("User with username exception: {}", ex.getMessage());
        return ResponseEntity.status(409).body(ErrorResponse.builder()
                .code(409)
                .message(ex.getMessage())
                .error("User Already Exists")
                .timestamp(LocalDateTime.now())
                .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(500).body(ErrorResponse.builder()
                .code(500)
                .message("An unexpected error occurred")
                .error("Internal Server Error")
                .timestamp(LocalDateTime.now())
                .build());
    }
}
