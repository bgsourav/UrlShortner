package com.bgsourav.urlshortener.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.bgsourav.urlshortener.dto.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidUrlException.class)
    public ResponseEntity<ErrorResponse> handleInvalidUrl(InvalidUrlException exception) {
        return ResponseEntity.badRequest().body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldError().getDefaultMessage();
        return ResponseEntity.badRequest().body(new ErrorResponse(message));
    }

    @ExceptionHandler(AliasConflictException.class)
    public ResponseEntity<ErrorResponse> handleAliasConflict(AliasConflictException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(LinkNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleLinkNotFound(LinkNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(exception.getMessage()));
    }
}
