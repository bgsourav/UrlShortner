package com.bgsourav.urlshortener.exception;

public class LinkNotFoundException extends RuntimeException {

    public LinkNotFoundException(String code) {
        super("Short link not found: " + code);
    }
}
