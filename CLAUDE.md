# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Start the database (required before running the app)
docker-compose up -d

# Build
./mvnw clean install

# Run the application
./mvnw spring-boot:run

# Run all tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=PostServiceImplTest

# Run a single test method
./mvnw test -Dtest=PostServiceImplTest#methodName
```

The app runs on `http://localhost:8080`. Swagger UI is available at `/swagger-ui.html`.

## Architecture

Standard layered Spring Boot REST API with JWT authentication.

**Package layout** (`com.fnavas.blogengine`):
- `api/` — REST controllers (Auth, Post, Comment, User)
- `service/` — Business logic interfaces and `*Impl` implementations
- `entity/` — JPA entities; `BaseEntity` provides `id`, `createdAt`, `updatedAt` for all entities
- `dto/request/` and `dto/response/` — API input/output contracts (never expose entities directly)
- `repository/` — Spring Data JPA repositories
- `mapper/` — MapStruct mappers connecting entities ↔ DTOs
- `security/` — JWT filter, custom entry point/access-denied handler, `PostSecurity`/`UserSecurity` for method-level `@PreAuthorize` checks
- `configuration/` — `SecurityConfig`, `ApplicationConfig`, `OpenApiConfig`
- `exception/` — `GlobalHandlerException` (`@RestControllerAdvice`) + domain exceptions + `ErrorResponse`

**Security model:**
- JWT Bearer token (1-hour expiration); stateless session
- Two roles: `ROLE_USER` and `ROLE_ADMIN`
- Public: `GET /api/v1/posts/**`, `/api/v1/auth/**`, `/api/v1/users` (registration), Swagger endpoints
- `PostSecurity`, `UserSecurity`, and `CommentSecurity` beans are injected into `@PreAuthorize` expressions to enforce ownership checks (users can only modify their own posts/comments/accounts)
- `@PreAuthorize` checks live on service methods (not controllers) for posts/comments; controllers have a coarser role check as a first gate
- Use `hasAnyRole('ADMIN', 'USER')` (no `ROLE_` prefix) — `hasAnyRole` adds the prefix automatically

**Testing strategy:**
- Unit tests use `@ExtendWith(MockitoExtension.class)` with mocked repositories
- Integration tests (`*IT.java`) use `@SpringBootTest` with H2 in-memory database
- Controller tests use `@WebMvcTest` with Spring Security test support

## Infrastructure

MySQL 8.0 runs via Docker on port **3307** (mapped from container's 3306). The app connects with credentials `blogengine/blogengine` to database `blogenginedb`.

`src/main/resources/data.sql` seeds 10 users (1 admin, 9 regular), sample posts, and comments on every startup (`spring.jpa.hibernate.ddl-auto=create`, so the schema is recreated each run).

## Known Issues

> See `CODE_REVIEW.md` for the full detailed report.

All critical bugs and high-priority gaps have been resolved. The project compiles and all 60 tests pass.

## Key Dependencies

- **JJWT** — JWT token generation/validation
- **MapStruct 1.6.3** — compile-time DTO mapping (annotation processor must run before compilation)
- **Lombok** — boilerplate reduction (also an annotation processor)
- **SpringDoc OpenAPI** — Swagger UI
- **H2** — test-scoped in-memory database for integration tests
