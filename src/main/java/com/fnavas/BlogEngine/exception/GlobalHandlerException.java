package com.fnavas.BlogEngine.exception;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class GlobalHandlerException {

    @ExceptionHandler(value = PostNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePostNotFoundException(PostNotFoundException ex) {
        log.error("Post not found exception: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setCode(404);
        errorResponse.setMessage(ex.getMessage());
        errorResponse.setError("Post Not Found");
        errorResponse.setTimestamp(LocalDateTime.now());
        return ResponseEntity.status(404).body(errorResponse);
    }

    @ExceptionHandler(value = UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex) {
        log.error("User not found exception: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setCode(404);
        errorResponse.setMessage(ex.getMessage());
        errorResponse.setError("User Not Found");
        errorResponse.setTimestamp(LocalDateTime.now());
        return ResponseEntity.status(404).body(errorResponse);
    }

    @ExceptionHandler(value = UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(UnauthorizedException ex) {
        log.error("Unauthorized exception: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setCode(401);
        errorResponse.setMessage(ex.getMessage());
        errorResponse.setError("Unauthorized");
        errorResponse.setTimestamp(LocalDateTime.now());
        return ResponseEntity.status(401).body(errorResponse);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        log.error("Internal server error: {}", ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setCode(500);
        errorResponse.setMessage(ex.getMessage());
        errorResponse.setError("Internal Server Error");
        errorResponse.setTimestamp(LocalDateTime.now());
        return ResponseEntity.status(500).body(errorResponse);
    }
}
