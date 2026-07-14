package com.bgsourav.urlshortener.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import com.bgsourav.urlshortener.domain.Link;
import com.bgsourav.urlshortener.dto.ShortenRequest;
import com.bgsourav.urlshortener.dto.ShortenResponse;
import com.bgsourav.urlshortener.exception.LinkNotFoundException;
import com.bgsourav.urlshortener.repository.LinkRepository;

@ExtendWith(MockitoExtension.class)
class LinkServiceTest {

    @Mock
    private LinkRepository linkRepository;

    @Mock
    private ShortCodeGenerator shortCodeGenerator;

    private LinkService linkService;

    @BeforeEach
    void setUp() {
        linkService = new LinkService(linkRepository, shortCodeGenerator, "http://localhost:8080");
    }

    @Test
    void createsAndReturnsAShortLink() {
        when(linkRepository.findFirstByNormalizedUrl("https://example.com/page")).thenReturn(Optional.empty());
        when(shortCodeGenerator.generate()).thenReturn("abc1234");
        when(linkRepository.saveAndFlush(any(Link.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ShortenResponse response = linkService.create(new ShortenRequest("https://example.com/page", null));

        assertThat(response).isEqualTo(new ShortenResponse(
                "abc1234", "http://localhost:8080/abc1234", "https://example.com/page"));
    }

    @Test
    void returnsTheExistingCodeForEquivalentUrls() {
        Link existingLink = new Link("abc1234", "http://Example.com", "http://example.com", null);
        when(linkRepository.findFirstByNormalizedUrl("http://example.com")).thenReturn(Optional.of(existingLink));

        ShortenResponse response = linkService.create(new ShortenRequest("http://example.com/", null));

        assertThat(response.code()).isEqualTo("abc1234");
        verify(shortCodeGenerator, never()).generate();
        verify(linkRepository, never()).saveAndFlush(any());
    }

    @Test
    void retriesWhenGeneratedCodeCollides() {
        when(linkRepository.findFirstByNormalizedUrl("https://example.com/page")).thenReturn(Optional.empty());
        when(shortCodeGenerator.generate()).thenReturn("taken01", "free123");
        when(linkRepository.saveAndFlush(any(Link.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate code"))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ShortenResponse response = linkService.create(new ShortenRequest("https://example.com/page", null));

        assertThat(response.code()).isEqualTo("free123");
        verify(shortCodeGenerator, times(2)).generate();
        verify(linkRepository, times(2)).saveAndFlush(any(Link.class));
    }

    @Test
    void resolvesALinkAndRecordsTheAccess() {
        Link link = new Link("abc1234", "https://example.com/page", "https://example.com/page", null);
        when(linkRepository.findByCode("abc1234")).thenReturn(Optional.of(link));

        String longUrl = linkService.resolve("abc1234");

        assertThat(longUrl).isEqualTo("https://example.com/page");
        assertThat(link.getClickCount()).isEqualTo(1);
        assertThat(link.getLastAccessedAt()).isNotNull();
    }

    @Test
    void rejectsAnUnknownCode() {
        when(linkRepository.findByCode("missing")).thenReturn(Optional.empty());

        org.assertj.core.api.Assertions.assertThatThrownBy(() -> linkService.resolve("missing"))
                .isInstanceOf(LinkNotFoundException.class);
    }
}
