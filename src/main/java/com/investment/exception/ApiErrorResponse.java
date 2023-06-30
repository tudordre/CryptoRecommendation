package com.investment.exception;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public record ApiErrorResponse(HttpStatus status,
                               int error_code,
                               String message,
                               String path,
                               LocalDateTime timeStamp
) {
}
