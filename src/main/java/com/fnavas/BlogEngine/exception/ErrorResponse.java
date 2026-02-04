package com.fnavas.BlogEngine.exception;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ErrorResponse {
    private int code;
    private String message;
    private String error;
    private LocalDateTime timestamp;
}
