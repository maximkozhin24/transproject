package com.logistics.logisticsapp.dto;

import java.time.LocalDateTime;

public class ErrorResponse {

    private String message;
    private String errorCode;
    private int status;
    private LocalDateTime timestamp;

    public ErrorResponse(String message, int status, String errorCode) {
        this.message = message;
        this.status = status;
        this.errorCode = errorCode;
        this.timestamp = LocalDateTime.now();
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}