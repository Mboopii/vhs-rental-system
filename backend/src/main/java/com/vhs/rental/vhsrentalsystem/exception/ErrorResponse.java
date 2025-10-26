package com.vhs.rental.vhsrentalsystem.exception;

import lombok.Data;

import java.time.LocalDateTime;

/*
 * Simple DTO used to structure error responses sent back to the client.
 */
@Data
public class ErrorResponse {
    private int statusCode;
    private LocalDateTime timestamp;
    private String message;
    private String description;

    public ErrorResponse(int statusCode, String message, String description) {
        this.statusCode = statusCode;
        this.message = message;
        this.description = description;
        this.timestamp = LocalDateTime.now();
    }
}
