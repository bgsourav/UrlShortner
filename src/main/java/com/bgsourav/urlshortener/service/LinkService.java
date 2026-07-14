package com.bgsourav.urlshortener.service;

import java.net.URI;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.bgsourav.urlshortener.domain.Link;
import com.bgsourav.urlshortener.dto.ShortenRequest;
import com.bgsourav.urlshortener.dto.ShortenResponse;
import com.bgsourav.urlshortener.repository.LinkRepository;

@Service
public class LinkService {

    private static final int MAX_CODE_GENERATION_ATTEMPTS = 5;

    private final LinkRepository linkRepository;
    private final ShortCodeGenerator shortCodeGenerator;
    private final String baseUrl;

    public LinkService(
            LinkRepository linkRepository,
            ShortCodeGenerator shortCodeGenerator,
            @Value("${app.base-url}") String baseUrl) {
        this.linkRepository = linkRepository;
        this.shortCodeGenerator = shortCodeGenerator;
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }

    public ShortenResponse create(ShortenRequest request) {
        String normalizedUrl = normalize(request.url());
        return linkRepository.findFirstByNormalizedUrl(normalizedUrl)
                .map(this::toResponse)
                .orElseGet(() -> createLink(request.url(), normalizedUrl));
    }

    private ShortenResponse createLink(String longUrl, String normalizedUrl) {
        for (int attempt = 0; attempt < MAX_CODE_GENERATION_ATTEMPTS; attempt++) {
            Link link = new Link(shortCodeGenerator.generate(), longUrl, normalizedUrl, null);
            try {
                return toResponse(linkRepository.saveAndFlush(link));
            } catch (DataIntegrityViolationException ignored) {
                // A concurrent insert claimed the generated code.
            }
        }
        throw new IllegalStateException("Unable to generate a unique short code");
    }

    private ShortenResponse toResponse(Link link) {
        return new ShortenResponse(link.getCode(), baseUrl + "/" + link.getCode(), link.getLongUrl());
    }

    private String normalize(String url) {
        URI uri = URI.create(url);
        if (uri.getScheme() == null || uri.getHost() == null) {
            return url;
        }

        String path = uri.getRawPath();
        if (path != null && path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        StringBuilder normalized = new StringBuilder()
                .append(uri.getScheme().toLowerCase(Locale.ROOT))
                .append("://");
        if (uri.getRawUserInfo() != null) {
            normalized.append(uri.getRawUserInfo()).append('@');
        }
        normalized.append(uri.getHost().toLowerCase(Locale.ROOT));
        if (uri.getPort() != -1) {
            normalized.append(':').append(uri.getPort());
        }
        if (path != null) {
            normalized.append(path);
        }
        if (uri.getRawQuery() != null) {
            normalized.append('?').append(uri.getRawQuery());
        }
        if (uri.getRawFragment() != null) {
            normalized.append('#').append(uri.getRawFragment());
        }
        return normalized.toString();
    }
}
