# Blog Engine REST API

A secure, production-ready RESTful API for a blogging platform built with Spring Boot 4. Demonstrates layered architecture, JWT authentication, role-based access control, and comprehensive test coverage.

## Features

- **JWT Authentication** — Stateless Bearer token auth with 1-hour expiration
- **Role-Based Access Control** — `ROLE_USER` and `ROLE_ADMIN` with method-level `@PreAuthorize`
- **Ownership checks** — Users can only modify their own posts, comments, and accounts
- **Input validation** — Bean Validation (JSR-380) on all request DTOs
- **Content filtering** — Filter posts by author and title using JPA Specifications
- **Pagination** — All list endpoints return `Page<T>` with configurable `page`, `size`, and `sort` parameters
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
| Infrastructure | Docker, Docker Compose |

---

## Running the Application

The project ships with four Spring profiles. Each one is designed for a specific scenario:

| Profile | Database | Schema | Seed data | Use case |
|---------|----------|--------|-----------|----------|
| `dev` | MySQL (Docker, port 3307) | Recreated on startup | Yes | Local development |
| `test` | H2 in-memory | Created/dropped per run | No | Automated tests |
| `prod` | MySQL (external or Docker) | Updated on startup | No | Production / demo |

---

### Option 1 — Local development (`dev` profile)

The `dev` profile is active by default. It connects to a MySQL 8.0 container on port `3307`, drops and recreates the schema on every startup, and seeds the database with sample users, posts, and comments.

**Prerequisites:** Java 17+, Docker

```bash
# 1. Start the MySQL container
docker compose up -d

# 2. Verify the container is healthy (optional)
docker compose ps

# 3. Run the application
./mvnw spring-boot:run
```

The API is available at `http://localhost:8080`.
Swagger UI: `http://localhost:8080/swagger-ui.html`

**Seeded credentials:**

All seeded users share the same password: `1234`

| Username | Role |
|----------|------|
| `admin` | ADMIN |
| `fnavas` | USER |
| `sarah_jenkins` | USER |
| `michael_smith` | USER |
| `emily_rose` | USER |
| `david_miller` | USER |
| `jessica_vance` | USER |
| `ryan_cooper` | USER |
| `olivia_p` | USER |
| `kevin_dev` | USER |
| `linda_blair` | USER |
| `brian_foster` | USER |
| `megan_fox` | USER |
| `chris_evans` | USER |
| `sophia_lane` | USER |
| `peter_parker` | USER |
| `anna_scott` | USER |
| `robert_downey` | USER |
| `lucy_liu` | USER |
| `tom_hanks` | USER |

**Stop the database:**
```bash
docker compose down
```

---

### Option 2 — Automated tests (`test` profile)

The `test` profile uses an H2 in-memory database. No Docker or external database is required. The profile is activated automatically when running tests.

**Prerequisites:** Java 17+

```bash
# Run the full test suite
./mvnw test

# Run a single test class
./mvnw test -Dtest=PostServiceImplTest

# Run a single test method
./mvnw test -Dtest=PostServiceImplTest#createPost_adminRole_returnPostResponse
```

The test report is generated at `target/surefire-reports/`.

---

### Option 3 — Full Docker stack (`prod` profile)

Builds the application image and runs both the app and the database as Docker containers. Uses the `prod` profile: no seed data, schema is created or updated automatically on first run, all sensitive values come from environment variables.

**Prerequisites:** Docker

```bash
# 1. Create your local environment file from the template
cp .env.example .env
```

Open `.env` and fill in the required values:

```env
DB_NAME=blogenginedb
DB_USERNAME=blogengine
DB_PASSWORD=your_secure_password

# Generate a strong secret with:  openssl rand -base64 32
JWT_SECRET=your_base64_encoded_secret
```

```bash
# 2. Build the application image and start all services
docker compose -f docker-compose.prod.yml --env-file .env up -d --build

# 3. Check that both containers are running and healthy
docker compose -f docker-compose.prod.yml ps

# 4. Follow application logs
docker compose -f docker-compose.prod.yml logs -f app
```

The API is available at `http://localhost:8080`.
Swagger UI: `http://localhost:8080/swagger-ui.html`

**Useful commands:**

```bash
# Stop all containers (database volume is preserved)
docker compose -f docker-compose.prod.yml down

# Stop and delete the database volume (all data lost)
docker compose -f docker-compose.prod.yml down -v

# Rebuild the image after a code change
docker compose -f docker-compose.prod.yml up -d --build app
```

---

### Option 4 — JAR with an external database (`prod` profile)

Build a self-contained JAR and point it at any existing MySQL 8.0 instance using environment variables. Useful for deploying to a remote server or a cloud VM.

**Prerequisites:** Java 17+, a running MySQL 8.0 instance

```bash
# 1. Build the JAR (tests are skipped for speed)
./mvnw clean package -DskipTests

# 2. Export the required environment variables
export SPRING_PROFILES_ACTIVE=prod
export DB_URL=jdbc:mysql://<host>:<port>/<database>?createDatabaseIfNotExist=true&serverTimezone=UTC
export DB_USERNAME=<db_user>
export DB_PASSWORD=<db_password>
export JWT_SECRET=<base64_encoded_secret>

# 3. Run the application
java -jar target/BlogEngine-0.0.1-SNAPSHOT.jar
```

Or pass them inline in a single command:

```bash
SPRING_PROFILES_ACTIVE=prod \
DB_URL=jdbc:mysql://db.example.com:3306/blogenginedb?createDatabaseIfNotExist=true&serverTimezone=UTC \
DB_USERNAME=blogengine \
DB_PASSWORD=your_secure_password \
JWT_SECRET=your_base64_encoded_secret \
java -jar target/BlogEngine-0.0.1-SNAPSHOT.jar
```

---

## Environment Variables Reference

| Variable | Description | Required |
|----------|-------------|:--------:|
| `JWT_SECRET` | Base64-encoded HS256 signing key (min 256-bit / 32 bytes). Generate with: `openssl rand -base64 32` | prod only |
| `DB_URL` | Full JDBC connection string | prod only |
| `DB_USERNAME` | Database username | prod only |
| `DB_PASSWORD` | Database password | prod only |

> In `dev` mode none of these are required — defaults are pre-configured in `application-dev.yml`.
> Never commit `.env` to version control. It is already listed in `.gitignore`.

---

## API Endpoints

### Authentication
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/api/v1/auth` | Public | Login, returns JWT token |

### Users
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/v1/users` | Public | List users — supports `?username=`, `?page=`, `?size=`, `?sort=` |
| GET | `/api/v1/users/{id}` | Public | Get user by ID |
| POST | `/api/v1/users` | Public | Register new user |
| PUT | `/api/v1/users/{id}` | Owner / Admin | Update user |
| DELETE | `/api/v1/users/{id}` | Owner / Admin | Delete user |

### Posts
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/v1/posts` | Public | List posts — supports `?author=`, `?title=`, `?page=`, `?size=`, `?sort=` |
| GET | `/api/v1/posts/{id}` | Public | Get post with comments |
| POST | `/api/v1/posts` | Authenticated | Create post |
| PUT | `/api/v1/posts/{id}` | Author / Admin | Update post |
| DELETE | `/api/v1/posts/{id}` | Author / Admin | Delete post |

### Comments
| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/api/v1/posts/{postId}/comments` | Public | List comments — supports `?page=`, `?size=`, `?sort=` |
| POST | `/api/v1/posts/{postId}/comments` | Authenticated | Create comment |
| PUT | `/api/v1/posts/{postId}/comments/{id}` | Author / Admin | Update comment |
| DELETE | `/api/v1/posts/{postId}/comments/{id}` | Author / Admin | Delete comment |

---

## Architecture

```
com.fnavas.blogengine/
├── api/           # REST controllers
├── service/       # Business logic (interfaces + Impl)
├── entity/        # JPA entities (BaseEntity with audit fields)
├── dto/           # Request/Response records (immutable)
├── repository/    # Spring Data JPA repositories
├── mapper/        # MapStruct entity ↔ DTO mappers
├── security/      # JWT filter, custom entry points, ownership beans
├── configuration/ # SecurityConfig, ApplicationConfig, OpenApiConfig
└── exception/     # GlobalHandlerException + domain exceptions
```

## Security Model

- JWT Bearer token extracted and validated on every request via `JwtAuthenticationFilter`
- Public endpoints: `GET /posts/**`, `GET /users/**`, `POST /users`, `/auth/**`, Swagger
- `PostSecurity`, `UserSecurity`, and `CommentSecurity` beans used in `@PreAuthorize` expressions for fine-grained ownership checks
- Authorization logic lives at the **service layer**, keeping controllers clean

## Database

MySQL 8.0 via Docker. The `dev` profile recreates the schema and seeds sample data (10 users, posts, and comments) on every startup. The `prod` profile applies schema changes incrementally (`ddl-auto=update`) with no seed data, keeping existing data intact across restarts.
