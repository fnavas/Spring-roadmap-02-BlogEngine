package com.fnavas.blogengine.security;

import com.fnavas.blogengine.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@Slf4j
public class CustomAuthEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException ex) throws IOException {
        log.info("[AuthenticationEntryPoint]-Unauthorized access attempt");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");

        String message = "Full authentication is required";

        response.getWriter().write(new ObjectMapper().writeValueAsString(ErrorResponse.builder()
                .code(401)
                .error("Unauthorized")
                .message(message)
                .timestamp(LocalDateTime.now())
                .build()
        ));
    }
}
