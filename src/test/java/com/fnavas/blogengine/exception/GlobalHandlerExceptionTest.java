package com.fnavas.blogengine.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GlobalHandlerExceptionTest {

    @InjectMocks
    private GlobalHandlerException handler;

    @Test
    void handleDataIntegrityViolationException_returns409() {
        DataIntegrityViolationException ex = new DataIntegrityViolationException("FK constraint violated");

        ResponseEntity<ErrorResponse> response = handler.handleDataIntegrityViolationException(ex);

        assertEquals(409, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(409, response.getBody().getCode());
        assertEquals("Conflict", response.getBody().getError());
        assertEquals("Cannot delete this resource because it has associated data", response.getBody().getMessage());
        assertNotNull(response.getBody().getTimestamp());
    }

    @Test
    void handleGenericException_returns500() {
        Exception ex = new RuntimeException("Something unexpected");

        ResponseEntity<ErrorResponse> response = handler.handleGenericException(ex);

        assertEquals(500, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().getCode());
        assertEquals("Internal Server Error", response.getBody().getError());
        assertEquals("An unexpected error occurred", response.getBody().getMessage());
        assertNotNull(response.getBody().getTimestamp());
    }
}
