package com.github.lisandrofernandez.hexagonal.infrastructure.in.api.controller;

import com.github.lisandrofernandez.hexagonal.infrastructure.in.api.common.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.time.Clock;
import java.time.LocalDateTime;

@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final Clock clock;

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException ex) {
        HttpStatusCode httpStatusCode = ex.getStatusCode();
        String message;

        if (httpStatusCode.is4xxClientError()) {
            log.warn(ex.getMessage(), ex);
            message = ex.getReason();
        } else if (httpStatusCode.is5xxServerError()) {
            log.error(ex.getMessage(), ex);
            if (httpStatusCode instanceof HttpStatus httpStatus) {
                message = httpStatus.getReasonPhrase();
            } else {
                message = "Server Error";
            }
        } else {
            message = "Invalid HttpStatusCode for the exception";
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, message, ex);
        }

        ErrorResponse errorResponse = ErrorResponse.builder()
                .httpStatusCode(httpStatusCode)
                .timestamp(LocalDateTime.now(clock))
                .message(message)
                .build();

        return new ResponseEntity<>(errorResponse, httpStatusCode);
    }
}
