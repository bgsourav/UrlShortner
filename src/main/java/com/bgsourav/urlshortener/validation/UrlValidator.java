package com.bgsourav.urlshortener.validation;

import java.net.URI;
import java.util.Locale;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.bgsourav.urlshortener.exception.InvalidUrlException;

@Component
public class UrlValidator {

    private static final int MAX_URL_LENGTH = 512;
    private static final Set<String> ALLOWED_SCHEMES = Set.of("http", "https");

    public void validate(String url) {
        if (url == null || url.isBlank()) {
            throw new InvalidUrlException("URL is required");
        }
        if (url.length() > MAX_URL_LENGTH) {
            throw new InvalidUrlException("URL must not exceed 512 characters");
        }

        URI uri;
        try {
            uri = URI.create(url);
        } catch (IllegalArgumentException exception) {
            throw new InvalidUrlException("URL must be a valid URI");
        }

        String scheme = uri.getScheme();
        if (scheme == null || !ALLOWED_SCHEMES.contains(scheme.toLowerCase(Locale.ROOT))) {
            throw new InvalidUrlException("URL scheme must be http or https");
        }
        if (uri.getHost() == null || uri.getHost().isBlank()) {
            throw new InvalidUrlException("URL must include a host");
        }
    }
}
