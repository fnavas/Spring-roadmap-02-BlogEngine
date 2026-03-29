# Blog Engine Security Overview

This document treats you as someone completely new to Spring Security. We will walk slowly through the *why* and *how* of every moving part before you dive into the codebase.

---
## 0. Spring Security in Plain Words
Spring Security is a set of filters and helper classes that intercept every HTTP request **before** it reaches your controllers. Each filter can decide whether a request should continue, be rejected, or be enriched (for example by attaching the authenticated user). Think of it as an airport checkpoint:
1. **Identity check** – confirms who you are (authentication).
2. **Permission check** – confirms what you are allowed to do (authorization).
3. **Escalations** – hands you a boarding pass or stops you with a clear error.

In this project the checkpoint logic lives mainly in two places:
- `configuration/SecurityConfig.java` describes the checkpoint lanes and what each lane allows.
- `security/` and `service/` packages provide the helpers that actually perform the checks.

---
## 1. Goals and Constraints
| Goal | Why it matters |
| --- | --- |
| Stateless JWT authentication | The backend never stores session data, so we can scale horizontally and rely on a signed token instead of server memory.
| Least privilege | Only administrators or the resource owners can mutate data, which prevents accidental updates from other users.
| Friendly error handling | Returning consistent `401/403` responses helps front-end developers understand what went wrong.
| Developer observability | Every critical branch logs at `INFO` and `DEBUG`, making it possible to trace security issues without attaching a debugger.

---
## 2. Request Lifecycle Cheat Sheet
1. **Incoming HTTP request** hits the `SecurityFilterChain` defined in `SecurityConfig`.
2. **CORS & CSRF** are evaluated (CSRF is disabled; CORS is fully open in dev).
3. **Route matching** decides whether the request is public or protected.
4. **JWT filter** (`JwtAuthenticationFilter`) tries to authenticate the user based on the `Authorization` header.
5. **Authentication provider** (`DaoAuthenticationProvider`) is used when a login request occurs (username/password).
6. **Controller** executes only if authentication/authorization succeeded.
7. **Service layer annotations** (`@PreAuthorize`) perform fine-grained authorization using helper beans.
8. **Exception handlers** (`CustomAuthEntryPoint`, `CustomAccessDeniedHandler`) fire if something fails.

These steps happen on *every* request, even if the controller itself never mentions Spring Security.

---
## 3. Component Map and Rationale
| Component | Location | Rationale |
| --- | --- | --- |
| `SecurityConfig` | `configuration/SecurityConfig.java` | Central place to express the HTTP rules: which endpoints are public, which filters run, and which authentication provider backs the app.
| `ApplicationConfig` | `configuration/ApplicationConfig.java` | Supplies reusable beans (`PasswordEncoder`, `AuthenticationManager`, `AuthenticationProvider`) so the rest of the codebase can inject them.
| `JwtAuthenticationFilter` | `security/JwtAuthenticationFilter.java` | Converts a raw `Bearer <token>` header into a fully populated `Authentication` object that downstream code can trust.
| `JwtService` | `service/JwtService.java` | Encapsulates token generation and validation, keeping crypto details in one class rather than scattered around.
| `CustomAuthEntryPoint` | `security/CustomAuthEntryPoint.java` | Handles unauthenticated access uniformly with `401` responses.
| `CustomAccessDeniedHandler` | `security/CustomAccessDeniedHandler.java` | Handles authenticated users who lack permissions with `403` responses.
| `PostSecurity` / `CommentSecurity` / `UserSecurity` | `security/*.java` | Provide readable, testable helpers for ownership checks inside `@PreAuthorize` annotations.

---
## 4. HTTP Filter Chain Explained (`SecurityConfig`)
1. **CSRF Disabled**: Because the app is stateless and expects JWTs on every call, CSRF tokens would add noise without improving security.
2. **CORS Setup**: `corsConfigurationSource()` currently allows any origin/method/header to simplify local testing. In production you would restrict origins to the front-end domains.
3. **Public Routes**: Specific `GET` and `POST` endpoints (posts list, user registration, auth, Swagger) are marked `permitAll()`. Everything else falls back to `authenticated()`.
4. **Session Policy**: `SessionCreationPolicy.STATELESS` instructs Spring not to create HTTP sessions, ensuring we never rely on server memory for identity.
5. **Exception Handling**: Registers custom handlers so we control the JSON/error message returned.
6. **JWT Filter Placement**: The filter runs *before* `UsernamePasswordAuthenticationFilter`, meaning it authenticates requests long before Spring considers form login or controller logic.

The end result is a predictable pipeline: request → filter chain → controller → service, with security gatekeeping up front.

---
## 5. Authentication Provider Deep Dive (`ApplicationConfig`)
- **`PasswordEncoder`**: Uses BCrypt (adaptive hash) so leaked password hashes are hard to brute-force.
- **`AuthenticationManager`**: Thin wrapper provided by Spring that orchestrates one or more authentication providers. We inject it into `AuthRestController`.
- **`DaoAuthenticationProvider`**: Connects Spring Security with your `UserRepository`. When a user tries to log in, this provider:
  1. Calls `CustomUserDetailService#loadUserByUsername` to fetch the user.
  2. Compares the submitted password with the stored BCrypt hash.
  3. Exposes the user’s authorities (roles) if authentication succeeds.

Because this provider is registered as a bean, unit tests can replace it or mock it as needed.

---
## 6. JWT Tokens: What, Why, and How
1. **Generation** (`JwtService#generateToken`): After a user logs in, we create a token with subject = username, `iat` = now, `exp` = now + 1 hour. No custom claims are added, keeping tokens small and easy to reason about.
2. **Signing Key**: Derived from `jwt.secret` (Base64). In production you override it via the `JWT_SECRET` environment variable so the secret never lives in source control.
3. **Validation**: Every request calls `JwtService#isTokenValid`, which ensures the username matches and the expiration date is still in the future.
4. **Security Context Population**: When the token checks out, `JwtAuthenticationFilter` builds a `UsernamePasswordAuthenticationToken` and saves it inside `SecurityContextHolder`. Controllers/services subsequently call `SecurityContextHolder.getContext().getAuthentication()` to know who the user is.

Because JWTs are stateless, revocation requires either rotating the secret or tracking invalidated tokens externally (not implemented here, but the architecture makes it easy to add later).

---
## 7. Roles, Ownership, and `@PreAuthorize`
- **Roles**: Stored as `ROLE_ADMIN` or `ROLE_USER` in the database, but referenced without the prefix in SpEL expressions (Spring automatically adds `ROLE_`).
- **Global Checks**: Controllers ensure “only authenticated users” reach certain methods, but the real enforcement happens in services via `@PreAuthorize`.
- **Ownership Helpers**:
  - `@postSecurity.isAuthor(#id, authentication.name)` loads the post and compares the author’s username to the authenticated username.
  - `@commentSecurity.isAuthor(#commentId, authentication.name)` does the same for comments.
  - `@userSecurity.isUser(#id, authentication.name)` ensures users can only modify their own profile.
- **Why in Services?** Keeping checks in services guarantees that even if another developer calls the service from a different controller (or scheduled job), the rule still holds.

Example from `PostServiceImpl`:
```java
@PreAuthorize("hasRole('ADMIN') or @postSecurity.isAuthor(#id, authentication.name)")
public PostResponse updatePost(Long id, PostCreateRequest postRequest) { ... }
```
This reads naturally (“admins or the author can edit”), which makes code reviews easier.

---
## 8. Error Handling Workflow
1. **Missing/invalid token** → `CustomAuthEntryPoint` returns `401` with the message "Full authentication is required".
2. **Insufficient permissions** → `CustomAccessDeniedHandler` returns `403` with "Access denied".
3. **Ownership checks failing** → still surface as `403` because the `@PreAuthorize` expression evaluates to `false`.
4. **Logging**: Both handlers log at `INFO` (high-level) and `DEBUG` (detailed context). Check the logs when debugging failed requests before touching the code.

---
## 9. Configuration Landmarks
| File | Purpose | Tips |
| --- | --- | --- |
| `src/main/resources/application.yml` | Sets the JWT secret (overridable), logging level, and active profile. | For non-dev environments, always set `JWT_SECRET` to a long random Base64 string. |
| `CLAUDE.md` | Lists commands to run the database, build, and tests. | Follow the order: database → `./mvnw spring-boot:run` → hit endpoints. |
| `docker-compose.yml` | Spins up MySQL with the credentials expected by the app. | Needed before running integration tests locally. |

---
## 10. How to Extend Safely
1. **New public route?** Add it to `SecurityConfig#authorizeHttpRequests` and document why it can be public.
2. **New protected resource?** Create a dedicated `*Security` helper for ownership checks if necessary, then add `@PreAuthorize` on service methods.
3. **Longer token lifetime?** Update `JwtService#generateToken`, remembering the security trade-offs (longer tokens are harder to revoke).
4. **Client-side debugging?** Use the commands in `docs/security-flow.md` to reproduce issues quickly.

By understanding *why* each component exists, you can confidently modify or extend the security layer without breaking the guardrails that keep the API safe.
