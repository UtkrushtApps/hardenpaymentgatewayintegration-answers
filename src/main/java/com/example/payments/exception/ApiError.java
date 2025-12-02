package com.example.payments.exception;

import java.time.OffsetDateTime;
import java.util.List;

public class ApiError {
    private final String message;
    private final List<String> details;
    private final int status;
    private final OffsetDateTime timestamp;

    public ApiError(String message, List<String> details, int status) {
        this.message = message;
        this.details = details;
        this.status = status;
        this.timestamp = OffsetDateTime.now();
    }

    public String getMessage() { return message; }
    public List<String> getDetails() { return details; }
    public int getStatus() { return status; }
    public OffsetDateTime getTimestamp() { return timestamp; }
}
