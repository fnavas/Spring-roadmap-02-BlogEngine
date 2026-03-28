package com.fnavas.blogengine.api;

import com.fnavas.blogengine.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@Slf4j
@Hidden
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public ResponseEntity<ErrorResponse> handleError(HttpServletRequest request) {
        Object statusAttr = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        int status = statusAttr != null ? (Integer) statusAttr : 500;

        String error;
        String message;

        switch (status) {
            case 401 -> { error = "Unauthorized";           message = "Full authentication is required"; }
            case 403 -> { error = "Forbidden";              message = "Access denied"; }
            case 404 -> { error = "Not Found";              message = "The requested resource was not found"; }
            case 405 -> { error = "Method Not Allowed";     message = "HTTP method not supported for this endpoint"; }
            default  -> { error = "Internal Server Error";  message = "An unexpected error occurred"; }
        }

        log.warn("[ErrorController] Handled error - status: {}, uri: {}",
                status, request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI));

        return ResponseEntity.status(status).body(ErrorResponse.builder()
                .code(status)
                .error(error)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build());
    }
}
