package com.bgsourav.urlshortener.domain;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(name = "links", indexes = @Index(name = "idx_links_code", columnList = "code", unique = true))
public class Link {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String longUrl;

    @Column(nullable = false)
    private String normalizedUrl;

    private String alias;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private long clickCount = 0;

    private Instant lastAccessedAt;

    protected Link() {
    }

    public Link(String code, String longUrl, String normalizedUrl, String alias) {
        this.code = code;
        this.longUrl = longUrl;
        this.normalizedUrl = normalizedUrl;
        this.alias = alias;
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getLongUrl() {
        return longUrl;
    }

    public String getNormalizedUrl() {
        return normalizedUrl;
    }

    public String getAlias() {
        return alias;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public long getClickCount() {
        return clickCount;
    }

    public Instant getLastAccessedAt() {
        return lastAccessedAt;
    }

    public void recordAccess(Instant accessedAt) {
        clickCount++;
        lastAccessedAt = accessedAt;
    }
}
