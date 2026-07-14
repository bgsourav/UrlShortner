package com.bgsourav.urlshortener.dto;

import com.bgsourav.urlshortener.validation.ValidAlias;

public record ShortenRequest(String url, @ValidAlias String alias) {
}
