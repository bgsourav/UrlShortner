# URL Shortener

A small Spring Boot service that creates short URLs and redirects them to their original destinations.

## Install

Requires Java 21. The Maven wrapper downloads the required Maven version automatically.

```bash
./mvnw clean package
```

## Run

```bash
./mvnw spring-boot:run
```

The service runs on `http://localhost:8080`. Create a link with `POST /shorten`:

```json
{
  "url": "https://example.com"
}
```

Use the returned `shortUrl`, or request `GET /{code}`, to receive a permanent redirect. Link counters are available at `GET /{code}/stats`.

## Test

```bash
./mvnw test
./mvnw test -Dtest=LinkServiceTest
```

## Design decisions

- Codes are seven-character base62 values from `SecureRandom`, giving a 62^7 (~3.5 trillion) space. The database unique constraint and bounded retry protect against collisions.
- Generated links are idempotent for the same normalized URL; the original URL is retained for redirects. Custom aliases always create a separate mapping and must be unique.
- Redirects use HTTP 301 because short links are permanent mappings.
- URL validation accepts only `http` and `https` URLs with a host, caps input at 512 characters, and makes no outbound reachability checks.

TODO: raise the 512-character URL limit when product requirements warrant it.
