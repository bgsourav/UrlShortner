package com.bgsourav.urlshortener.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.bgsourav.urlshortener.domain.Link;

@DataJpaTest
class LinkRepositoryTest {

    @Autowired
    private LinkRepository linkRepository;

    @Test
    void findsLinksByCodeAndLongUrl() {
        Link link = linkRepository.save(new Link("abc123", "https://example.com/page", null));

        assertThat(link.getId()).isNotNull();
        assertThat(link.getCreatedAt()).isNotNull();
        assertThat(link.getClickCount()).isZero();
        assertThat(linkRepository.findByCode("abc123")).contains(link);
        assertThat(linkRepository.findByLongUrl("https://example.com/page")).containsExactly(link);
        assertThat(linkRepository.findByLongUrl("https://example.com/other")).isEqualTo(List.of());
    }
}
