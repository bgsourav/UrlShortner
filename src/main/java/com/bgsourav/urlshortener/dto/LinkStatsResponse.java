package com.bgsourav.urlshortener.dto;

import java.time.Instant;

public record LinkStatsResponse(String code, String longUrl, long clicks, Instant createdAt, Instant lastAccessedAt) {
}
