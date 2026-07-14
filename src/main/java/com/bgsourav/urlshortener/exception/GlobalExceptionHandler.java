package com.bgsourav.urlshortener.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AliasConflictException.class)
    public ResponseEntity<Void> handleAliasConflict() {
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @ExceptionHandler(LinkNotFoundException.class)
    public ResponseEntity<Void> handleLinkNotFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
