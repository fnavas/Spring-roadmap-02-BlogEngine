# Step-by-Step Security Flow (Beginner Edition)

Use this narrative to imagine what happens from the moment a client crafts an HTTP request until the application either serves data or blocks the call. Nothing is assumed—you will see the “why” behind every instruction.

---

## 1. Registration: Establishing a Real User
**Purpose:** Give someone a database identity before tokens even exist.

1. Client sends `POST /api/v1/users` with username/password.
2. `UserServiceImpl#createUser` executes:
   - Confirms the username is unique (prevents impersonation of existing accounts).
   - Forces the role `ROLE_USER` so nobody can self-register as admin.
   - Hashes the password with BCrypt. Hashing means the database never stores the raw password; even if leaked, the attacker faces a slow brute-force process.
3. The API returns the new user details (without the password). No token yet, because authentication happens in the next stage.

Takeaway: registration is all about **data integrity**. No authentication, no JWT, just creating trustworthy records.

---

## 2. Login: From Credentials to Token
**Goal:** Exchange username/password for a signed JWT the client can present later.

```
Client
  │  POST /api/v1/auth {username,password}
  ▼
AuthRestController
  │  authenticationManager.authenticate(...)
  ▼
DaoAuthenticationProvider
  │  CustomUserDetailService → UserRepository
  │  (password check via BCrypt)
  ▼
JwtService.generateToken()
  │  Sign token with jwt.secret
  ▼
Response: { "token": "Bearer <jwt>" }
```

Detailed reasoning:
1. `AuthRestController#login` wraps the credentials in `UsernamePasswordAuthenticationToken` and hands it to `AuthenticationManager`.
2. `DaoAuthenticationProvider` loads the user via `CustomUserDetailService` and compares passwords using the encoder bean. Only if the hashes match does the flow continue.
3. `JwtService` builds a compact token with:
   - **Subject (`sub`)** = username (tells us who the user claims to be).
   - **Issued-at (`iat`)** = current time (helps debugging and expiry checks).
   - **Expiration (`exp`)** = `iat + 1 hour` (limits risk if the token leaks).
4. The token is signed with the secret key so that the server can later detect tampering. Clients must store this token (usually in memory or secure storage).

Takeaway: after login we rely entirely on the token; the server forgets the session immediately.

---

## 3. Requesting a Protected Resource
**Scenario:** Client wants to create a post.

1. Client sends `POST /api/v1/posts` with header `Authorization: Bearer <token>`.
2. `SecurityFilterChain` sees the route is not public, so the request must be authenticated.
3. `JwtAuthenticationFilter` runs before controllers:
   - Extracts the token from the header.
   - Calls `JwtService.extractUsername` and `JwtService.isTokenValid`.
   - Loads `UserDetails` so it knows the roles associated with the username.
   - If valid, creates a `UsernamePasswordAuthenticationToken` and saves it in `SecurityContextHolder`.
4. Because authentication succeeded, the request proceeds to the controller and then to `PostServiceImpl#createPost`.
5. Inside the service method, `@PreAuthorize("hasAnyRole('ADMIN', 'USER')")` runs **before** the body executes. If the user lacks either role, Spring aborts the call with `403`.
6. Assuming the check passes, the service uses the authenticated username to set the post author.

Takeaway: every protected route depends on the JWT filter populating `SecurityContextHolder`. Without that context, `@PreAuthorize` would see an anonymous user and fail.

---

## 4. Ownership Enforcement for Updates/Deletes
Let’s analyze `PostServiceImpl#updatePost(Long id, ...)` since it mixes roles with custom checks.

1. `@PreAuthorize("hasRole('ADMIN') or @postSecurity.isAuthor(#id, authentication.name)")` executes *before* any repository call:
   - If the user has `ROLE_ADMIN`, authorization succeeds immediately.
   - Otherwise Spring evaluates `@postSecurity.isAuthor(...)`.
2. `PostSecurity#isAuthor` loads the post from `PostRepository` and compares `post.getAuthor().getUsername()` with the `authentication.name` (set by the JWT filter).
3. Outcomes:
   - **True** → user is the author, so they can update/delete their own content.
   - **False** → user tried to touch someone else’s post; Spring throws `AccessDeniedException`, leading to a `403` response.

Why this pattern?
- We keep repository lookups encapsulated, so the expression stays readable.
- Tests can mock `PostSecurity` to simulate ownership without dealing with the entire service.
- Future changes (e.g., moderators) require editing the expression, not duplicating logic across methods.

The same idea applies to comments (`CommentSecurity`) and user profiles (`UserSecurity`).

---

## 5. What Happens When Things Go Wrong?
| Situation | Detection Point | Response |
| --- | --- | --- |
| Missing/invalid token | `JwtAuthenticationFilter` cannot parse/validate the header | Continues chain without authentication → later triggers `CustomAuthEntryPoint` (401, "Full authentication is required"). |
| Token expired | `JwtService.isTokenExpired` returns true | Request treated as unauthenticated, ends in `401`. |
| Authenticated but lacks role/ownership | `@PreAuthorize` evaluates to `false` | `CustomAccessDeniedHandler` responds with `403`, message "Access denied". |
| Resource ID not found | `*Security` helper can’t find the entity | Returns `false`, resulting in `403` (keeps attackers from learning whether the ID exists). |

Remember: `401` means “you are not logged in (or token invalid)”; `403` means “you are logged in but not allowed to perform this action”.

---

## 6. Practicing the Flow Locally
1. **Start infrastructure:**
   ```powershell
   docker-compose up -d
   ./mvnw spring-boot:run
   ```
2. **Login to get a token:**
   ```powershell
   $authBody = '{"username":"admin","password":"admin123"}'
   $token = (Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/v1/auth -Body $authBody -ContentType 'application/json').token
   ```
3. **Call a protected endpoint:**
   ```powershell
   Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/v1/posts `
     -Headers @{ Authorization = "Bearer $token" } `
     -ContentType 'application/json' `
     -Body '{"title":"demo","content":"demo"}'
   ```
4. **Trigger a 401 on purpose (no token):**
   ```powershell
   Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/v1/posts -ContentType 'application/json' -Body '{"title":"demo","content":"demo"}'
   ```
5. **Trigger a 403 (wrong owner):**
   - Authenticate as a non-admin user.
   - Attempt to delete someone else’s post; `@postSecurity.isAuthor` returns false.

Each exercise reinforces a section of the flow: registration, login, JWT validation, and authorization guards.

---

## 7. Mental Debugging Checklist (Slow Mode)
1. **Header present?** Inspect the request in a proxy or log; without `Authorization: Bearer ...` everything fails fast.
2. **Token integrity?** Decode the JWT at [jwt.io](https://jwt.io) and ensure the payload matches expectations (username, timestamps).
3. **Clock drift?** If working across containers/VMs, confirm server and client clocks differ by less than a few seconds.
4. **Role assigned?** Check the `role` column in the `users` table or log `authentication.getAuthorities()`.
5. **Ownership helpers?** Temporarily add DEBUG logs inside `PostSecurity/CommentSecurity/UserSecurity` to see which branch is taken.
6. **Is the route public?** Compare with the `requestMatchers` list in `SecurityConfig`—maybe you meant to allow anonymous access.

Working through this list systematically will almost always reveal why a request was rejected.

---

## 8. Key Files to Review Afterwards
- `configuration/SecurityConfig.java` – full HTTP policy and filter chain.
- `configuration/ApplicationConfig.java` – beans that wire authentication pieces together.
- `security/JwtAuthenticationFilter.java` – how tokens are parsed and injected into the context.
- `service/JwtService.java` – token generation/validation logic.
- `service/*ServiceImpl.java` – look for `@PreAuthorize` annotations to understand per-resource rules.

Master these files and you will be comfortable tracing any security-related issue from symptom back to source.
