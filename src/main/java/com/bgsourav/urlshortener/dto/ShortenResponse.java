package com.bgsourav.urlshortener.dto;

public record ShortenResponse(String code, String shortUrl, String longUrl) {
}
