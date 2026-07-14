URL shortener service: turns long URLs into short codes and redirects on lookup. A deliberately right-sized two-endpoint monolith — not microservices.

**Stack:** Java 21 (LTS), Spring Boot 3.5.x, Spring Data JPA, H2 (file-backed for dev). The datastore is swappable to Postgres by config — keep SQL portable, no H2-specific dialect in code.

**Build & run:** build with ./mvnw clean package, run with ./mvnw spring-boot:run, test with ./mvnw test, single test with ./mvnw test -Dtest=LinkServiceTest.

**Architecture:** layered — controller (thin, HTTP only) → service (business logic) → repository (Spring Data JPA) → domain (@Entity). DTOs live in dto, request validation in validation, exception-to-status mapping is centralized in exception/GlobalExceptionHandler (@RestControllerAdvice). Controllers never hold business logic; services never touch HTTP types.

**Conventions:** constructor injection only (no field injection); DTOs for every request/response body, never expose the entity; commit messages are lowercase, imperative, concise, with no conventional-commit prefixes (no feat: / chore:); one idea per commit; don't touch files outside the current commit's scope.

**Domain rules the agent must not get wrong:**


Short codes are base62 [A-Za-z0-9] generated with SecureRandom. Uniqueness is enforced by a **DB unique constraint** on code plus a bounded retry on collision — never assume generation alone guarantees uniqueness.
Reserved codes/aliases that must be rejected: shorten, health, stats. An alias must never shadow a real route.
URL validation allows only http/https. Reject javascript:, data:, file:. Do **not** make an outbound request to check reachability (SSRF risk).
Redirect is 301 per spec. Unknown code → 404. Alias collision → 409. Invalid URL → 400.
