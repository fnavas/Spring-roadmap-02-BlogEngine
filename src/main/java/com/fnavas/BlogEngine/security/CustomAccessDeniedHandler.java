package com.fnavas.BlogEngine.security;

import com.fnavas.BlogEngine.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException ex) throws IOException {
        log.info("[AuthenticationEntryPoint]-AccessDenied access attempt");
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json");

        ErrorResponse error = new ErrorResponse();
        error.setCode(403);
        error.setMessage(ex.getMessage());
        error.setError("Forbidden");
        error.setTimestamp(LocalDateTime.now());

        response.getWriter().write(new ObjectMapper().writeValueAsString(error));
    }
}
