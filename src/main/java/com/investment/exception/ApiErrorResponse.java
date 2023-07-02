package com.investment.exception;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

/**
 * Record that describes information about an error displayed in the endpoint responses
 */

public record ApiErrorResponse(HttpStatus status,
                               int error_code,
                               String message,
                               String path,
                               LocalDateTime timeStamp
) {
}
