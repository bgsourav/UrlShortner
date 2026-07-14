# URL Shortener

A deliberately small Spring Boot service for creating short links, redirecting them, and viewing basic click statistics.

## Requirements

- Java 21
- No separate Maven installation is needed; the Maven wrapper is included.

## Build, run, and test

```bash
./mvnw clean package
./mvnw spring-boot:run
./mvnw test
./mvnw test -Dtest=LinkServiceTest
```

The application starts on `http://localhost:8080`. Development data is stored in file-backed H2 under `data/`.

## API

Create a generated short link:

```bash
curl -X POST http://localhost:8080/shorten \
  -H 'Content-Type: application/json' \
  -d '{"url":"https://example.com/docs"}'
```

Create a custom alias:

```bash
curl -X POST http://localhost:8080/shorten \
  -H 'Content-Type: application/json' \
  -d '{"url":"https://example.com/docs","alias":"docs"}'
```

`GET /{code}` returns a `301` redirect. `GET /{code}/stats` returns the stored URL, click count, creation time, and last access time. Invalid URLs return `400`, an occupied alias returns `409`, and unknown links return `404`.

Operational endpoints are available at `/actuator/health` and `/actuator/metrics`. The service records link-creation counters by type and a successful-redirect counter.

## Design notes

Generated codes are seven-character base62 values from `SecureRandom`, giving a 62^7 (~3.5 trillion) space. Randomness is not treated as a guarantee: the database has a unique constraint and creation retries a collision a bounded number of times.

Generated links are idempotent for the same normalized URL, while custom aliases always create distinct mappings. This is convenient for retries, but it means click counts are attached to the normalized generated-link mapping rather than an individual creation event. The original URL is retained for redirecting.

Redirects use `301` because the assignment defines links as permanent. URL validation only accepts HTTP(S) URLs with a host, limits input to 512 characters, and never performs outbound reachability checks.

H2 keeps the take-home easy to run locally; the JPA mapping and SQL choices are intended to remain portable to Postgres. Test contexts use isolated in-memory H2 databases.

## Development note

AI was used as a scoped pair-programming tool for implementation and test generation, while the stack, architecture, domain rules, response behavior, alias bounds, URL cap, and review criteria were explicitly constrained. The code was kept as a layered monolith rather than expanded into speculative services.

The main open production concerns are per-click event storage for richer analytics, rate limiting, caching on the redirect path, access control around operational endpoints, and eventually separating redirect reads from write traffic.

TODO: raise the 512-character URL limit when product requirements warrant it.
