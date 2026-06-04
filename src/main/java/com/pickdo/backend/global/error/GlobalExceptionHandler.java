package com.pickdo.backend.global.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.pickdo.backend.global.response.ResponseEnvelope;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEnvelope<Void> handleIllegalStateException(IllegalStateException exception) {
        return ResponseEnvelope.error("INTERNAL_SERVER_ERROR", exception.getMessage(), null);
    }
}
