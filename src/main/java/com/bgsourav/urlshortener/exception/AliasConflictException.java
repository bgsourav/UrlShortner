package com.bgsourav.urlshortener.exception;

public class AliasConflictException extends RuntimeException {

    public AliasConflictException(String alias) {
        super("Alias is already in use: " + alias);
    }
}
