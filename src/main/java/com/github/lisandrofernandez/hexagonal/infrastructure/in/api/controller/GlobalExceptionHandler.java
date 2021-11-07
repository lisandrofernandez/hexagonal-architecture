package com.github.lisandrofernandez.hexagonal.infrastructure.in.api.controller;

import com.github.lisandrofernandez.hexagonal.infrastructure.in.api.common.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

import java.time.Clock;
import java.time.LocalDateTime;

import static java.util.Objects.requireNonNullElseGet;

@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final Clock clock;

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException ex, WebRequest request) {
        HttpStatus httpStatus = ex.getStatus();
        String message;

        if (httpStatus.is4xxClientError()) {
            message = ex.getReason();
        } else if (httpStatus.is5xxServerError()) {
            log.error(ex.getMessage(), ex);
            message = httpStatus.getReasonPhrase();
        } else {
            message = "invalid HttpStatus for the exception";
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, message, ex);
        }

        ErrorResponse errorResponse = ErrorResponse.builder()
                .httpStatus(httpStatus)
                .timestamp(LocalDateTime.now(clock))
                .message(requireNonNullElseGet(message, httpStatus::getReasonPhrase))
                .build();

        return new ResponseEntity<>(errorResponse, httpStatus);
    }
}
