package com.bgsourav.urlshortener.service;

import java.security.SecureRandom;

import org.springframework.stereotype.Service;

@Service
public class ShortCodeGenerator {

    private static final String BASE62 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int CODE_LENGTH = 7;

    private final SecureRandom random = new SecureRandom();

    public String generate() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int index = 0; index < CODE_LENGTH; index++) {
            code.append(BASE62.charAt(random.nextInt(BASE62.length())));
        }
        return code.toString();
    }
}
