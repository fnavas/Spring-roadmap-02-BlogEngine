package com.fnavas.blogengine.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@Schema(description = "Standard error response returned for all API errors")
public class ErrorResponse {
    @Schema(description = "HTTP status code", example = "404")
    private int code;
    @Schema(description = "Error detail message", example = "Post not found with id: 99")
    private String message;
    @Schema(description = "Error type", example = "Post Not Found")
    private String error;
    @Schema(description = "Timestamp of the error", example = "2026-03-28T10:30:00")
    private LocalDateTime timestamp;
}
