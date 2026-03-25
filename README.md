# Blog Engine REST API

A secure, production-ready RESTful API for a blogging platform built with Spring Boot 4. Demonstrates layered architecture, JWT authentication, role-based access control, and comprehensive test coverage.

## Features

- **JWT Authentication** — Stateless Bearer token auth with 1-hour expiration
- **Role-Based Access Control** — `ROLE_USER` and `ROLE_ADMIN` with method-level `@PreAuthorize`
- **Ownership checks** — Users can only modify their own posts, comments, and accounts
- **Input validation** — Bean Validation (JSR-380) on all request DTOs
- **Content filtering** — Filter posts by author and title using JPA Specifications
- **Centralized error handling** — `@RestControllerAdvice` with typed exceptions and consistent `ErrorResponse`
- **API documentation** — Swagger UI with Bearer token support

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Framework | Spring Boot 4, Spring MVC, Spring Security |
| Persistence | Spring Data JPA, Hibernate, MySQL 8.0 |
| Auth | JJWT (HS256), BCrypt |
| Mapping | MapStruct 1.6 |
| Testing | JUnit 5, Mockito, H2 (in-memory) |
| Docs | SpringDoc OpenAPI / Swagger UI |
| Infrastructure | Docker Compose |

## Getting Started

### Prerequisites

- Java 17+
- Docker (for MySQL)

### Run

```bash
# Start the database
docker-compose up -d

# Run the application
./mvnw spring-boot:run
```

The API is available at `http://localhost:8080`.
Swagger UI: `http://localhost:8080/swagger-ui.html`

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `JWT_SECRET` | Base64-encoded HS256 key (min 256-bit) | demo key (change in production) |

### Run Tests

```bash
./mvnw test
```

## API Endpoints

### Authentication
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/api/v1/auth` | Public | Login, returns JWT token |

### Users
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/v1/users` | Public | List users (or search by username) |
| GET | `/api/v1/users/{id}` | Public | Get user by ID |
| POST | `/api/v1/users` | Public | Register new user |
| PUT | `/api/v1/users/{id}` | Owner / Admin | Update user |
| DELETE | `/api/v1/users/{id}` | Owner / Admin | Delete user |

### Posts
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/v1/posts` | Public | List posts (filter: `?author=`, `?title=`) |
| GET | `/api/v1/posts/{id}` | Public | Get post with comments |
| POST | `/api/v1/posts` | Authenticated | Create post |
| PUT | `/api/v1/posts/{id}` | Author / Admin | Update post |
| DELETE | `/api/v1/posts/{id}` | Author / Admin | Delete post |

### Comments
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/v1/posts/{postId}/comments` | Public | List comments for a post |
| POST | `/api/v1/posts/{postId}/comments` | Authenticated | Create comment |
| PUT | `/api/v1/posts/{postId}/comments/{id}` | Author / Admin | Update comment |
| DELETE | `/api/v1/posts/{postId}/comments/{id}` | Author / Admin | Delete comment |

## Architecture

```
com.fnavas.blogengine/
├── api/          # REST controllers
├── service/      # Business logic (interfaces + Impl)
├── entity/       # JPA entities (BaseEntity with audit fields)
├── dto/          # Request/Response records (immutable)
├── repository/   # Spring Data JPA repositories
├── mapper/       # MapStruct entity ↔ DTO mappers
├── security/     # JWT filter, custom entry points, ownership beans
├── configuration/ # SecurityConfig, ApplicationConfig, OpenApiConfig
└── exception/    # GlobalHandlerException + domain exceptions
```

## Security Model

- JWT Bearer token extracted and validated on every request via `JwtAuthenticationFilter`
- Public endpoints: `GET /posts/**`, `GET /users/**`, `POST /users`, `/auth/**`, Swagger
- `PostSecurity`, `UserSecurity`, and `CommentSecurity` beans used in `@PreAuthorize` expressions for fine-grained ownership checks
- Authorization logic lives at the **service layer**, keeping controllers clean

## Database

MySQL 8.0 via Docker on port `3307`. Schema is recreated on each startup (`ddl-auto=create`) and seeded with 10 users (1 admin + 9 regular), sample posts, and comments.

```bash
docker-compose up -d
```

Credentials: `blogengine / blogengine` — database: `blogenginedb`
