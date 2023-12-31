package com.investment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Exception handling for all the exception thrown by the service.
 */
@ControllerAdvice
public class GlobalExceptionHandlerController {

    public static final String EUROPE_BUCHAREST = "Europe/Bucharest";

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiErrorResponse> handleCustomException(CustomException ex, WebRequest request) {
        return ResponseEntity.status(ex.getHttpStatus()).body(
                new ApiErrorResponse(ex.getHttpStatus(),
                        ex.getHttpStatus().value(),
                        ex.getMessage(),
                        ((ServletWebRequest) request).getRequest().getRequestURI(),
                        LocalDateTime.now(ZoneId.of(EUROPE_BUCHAREST))));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ApiErrorResponse(HttpStatus.BAD_REQUEST,
                        HttpStatus.BAD_REQUEST.value(),
                        ex.getMessage(),
                        ((ServletWebRequest) request).getRequest().getRequestURI(),
                        LocalDateTime.now(ZoneId.of(EUROPE_BUCHAREST))));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleException(Exception ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ApiErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Something went wrong",
                        ((ServletWebRequest) request).getRequest().getRequestURI(),
                        LocalDateTime.now(ZoneId.of(EUROPE_BUCHAREST))));
    }
}
