# Code Review — BlogEngine

**Fecha:** 2026-03-18
**Rama:** main
**Versión:** Spring Boot 4.0.2 / Java 17

---

> **Estado actual (2026-03-26):** todos los bugs críticos y los issues de alta/media prioridad han sido resueltos. Ver tabla de prioridades en la sección 9 para el estado detallado de cada item.

---

## Resumen Ejecutivo

El proyecto tiene una arquitectura bien estructurada y sigue buenas prácticas de Spring Boot. Sin embargo, hay **dos bugs críticos de compilación**, varios problemas de seguridad y múltiples inconsistencias que deben corregirse antes de considerar el proyecto listo para producción.

---

## 1. BUGS CRÍTICOS (impiden compilación o rompen funcionalidad)

### 1.1 Import incorrecto de ObjectMapper — `CustomAuthEntryPoint.java:11`

```java
// MAL — el paquete "tools.jackson" no existe en el classpath
import tools.jackson.databind.ObjectMapper;

// CORRECTO
import com.fasterxml.jackson.databind.ObjectMapper;
```

**Impacto:** Error de compilación. Cualquier intento de respuesta 401 lanzará una excepción, devolviendo un error genérico en lugar del JSON de error esperado.

---

### 1.2 Mismo import incorrecto — `CustomAccessDeniedHandler.java:12`

```java
// MAL
import tools.jackson.databind.ObjectMapper;

// CORRECTO
import com.fasterxml.jackson.databind.ObjectMapper;
```

**Impacto:** Idéntico al anterior. Los errores 403 no se formatearán correctamente.

---

### 1.3 Inconsistencia en firma y verificación JWT — `JwtService.java:26`

```java
// generateToken usa la cadena Base64 directamente (método deprecated)
.signWith(SignatureAlgorithm.HS256, SECRET_KEY)

// extractAllClaims usa getSigningKey() que decodifica el Base64
.setSigningKey(getSigningKey())
```

**Impacto:** Firma y verificación usan material de clave diferente. Los tokens generados pueden fallar en la validación. Debe usarse `signWith(getSigningKey())` de forma consistente.

---

## 2. PROBLEMAS DE SEGURIDAD

### 2.1 Clave JWT hardcodeada en el código fuente — `JwtService.java:18`

```java
private static final String SECRET_KEY = "Z3VlbnRhX2VzdGFfY2xhdmVfc3VwZXJfc2VndXJhX3BhcmFfandrX2RlbW9fMjAyNg==";
```

**Riesgo:** Cualquiera con acceso al repositorio puede firmar tokens válidos y suplantar cualquier usuario, incluyendo administradores.
**Solución:** Externalizar a variable de entorno o `application.properties` (no versionado):

```properties
# application.properties
app.jwt.secret=${JWT_SECRET}
app.jwt.expiration=3600000
```

---

### 2.2 Contraseñas en logs — `UserServiceImpl.java:32-33` y `UserRestController.java:47`

```java
// UserServiceImpl.java:32
log.debug("[createUser]-Service creating new user with username:" +
        " {} password:{}", userRequest.username(), userRequest.password());

// UserRestController.java:47
log.debug("[createUser]-RestController request to create a new user with username: {}" +
        " password: {}", userRequest.username(), userRequest.password());
```

**Riesgo:** Si el nivel de log se establece en DEBUG en producción, las contraseñas en texto plano quedan registradas en los logs.
**Solución:** Eliminar el campo `password` de estos mensajes de log.

---

### 2.3 Credenciales de base de datos hardcodeadas

- `application.properties:7-8` — credenciales MySQL en texto plano.
- `docker-compose.yml` — credenciales MySQL en texto plano.

**Solución:** Usar variables de entorno (`${DB_PASSWORD}`) y añadir ambos ficheros a `.gitignore` o usar un fichero `application-local.properties` no versionado.

---

### 2.4 `@ToString` en entidad con campo password — `User.java:16`

```java
@ToString  // genera toString() que incluye el campo password
public class User extends BaseEntity implements UserDetails {
    private String password;
    ...
}
```

**Riesgo:** Cualquier log que imprima un objeto `User` expone la contraseña (aunque sea hasheada).
**Solución:** `@ToString(exclude = "password")`.

---

### 2.5 Sin autenticación en endpoints de comentarios — `CommentRestController.java`

Los métodos `createComment`, `updateComment` y `deleteComment` no tienen `@PreAuthorize` en el controlador. La seguridad recae únicamente en la capa de servicio, lo que es válido pero reduce la visibilidad y puede llevar a errores si se refactoriza.

---

## 3. VALIDACIÓN DE ENTRADA

### 3.1 `@Valid` ausente en todos los controladores

`PostCreateRequest` tiene anotaciones de validación (`@NotBlank`, `@Size`) pero **nunca se activan** porque los controladores no tienen `@Valid` en los parámetros `@RequestBody`:

```java
// PostRestController.java:42 — MAL
public ResponseEntity<PostResponse> createPost(@RequestBody PostCreateRequest postRequest)

// CORRECTO
public ResponseEntity<PostResponse> createPost(@Valid @RequestBody PostCreateRequest postRequest)
```

**Afecta a:** `PostRestController`, `UserRestController`, `CommentRestController`, `AuthRestController`.

---

### 3.2 DTOs sin validación — `UserRegisterRequest.java` y `CommentRequest.java`

```java
// UserRegisterRequest — sin ninguna validación
public record UserRegisterRequest(
        String username,   // puede ser null, vacío, o 10000 caracteres
        String password    // sin longitud mínima ni máxima
) {}

// CommentRequest — sin validación
public record CommentRequest(
        String text        // puede ser null o vacío
) {}
```

**Solución:** Añadir `@NotBlank`, `@Size(min=3, max=50)` en username, `@Size(min=8)` en password, `@NotBlank @Size(max=1000)` en text.

---

## 4. INCONSISTENCIAS EN AUTORIZACIÓN

### 4.1 Formato de roles inconsistente entre controladores

```java
// UserRestController.java:54 — con prefijo ROLE_
@PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")

// PostRestController.java:41 — sin prefijo (correcto para hasAnyRole)
@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
```

`hasAnyRole()` añade el prefijo `ROLE_` automáticamente, así que `hasAnyRole('ROLE_USER')` busca `ROLE_ROLE_USER` y **nunca coincide**. El controlador de usuarios tiene el bug — las operaciones de actualización y borrado de usuarios serán denegadas a todos.

**Corrección:** Usar `hasAnyRole('USER', 'ADMIN')` uniformemente (sin prefijo).

---

### 4.2 Doble `@PreAuthorize` redundante (controlador + servicio)

Para Posts, la autorización está en el controlador (`hasAnyRole`) Y en el servicio (`@postSecurity.isAuthor`). El chequeo del controlador es demasiado permisivo y el del servicio es el que realmente importa. Considerar centralizar en la capa de servicio o eliminar la redundancia.

---

## 5. CALIDAD DE CÓDIGO

### 5.1 `updatedAt` manual en lugar de usar JPA — `UserServiceImpl.java:93`

```java
// MAL — gestión manual del timestamp
user.setUpdatedAt(LocalDateTime.now());
User updatedUser = userRepository.save(user);
```

`BaseEntity` ya tiene `@LastModifiedDate` (vía `@UpdateTimestamp` de Hibernate). Este `setUpdatedAt` manual es redundante y podría causar conflictos. **Eliminarlo**.

---

### 5.2 Constructor manual en lugar de usar el mapper — `CommentServiceImpl.java:63-66`

```java
// MAL — construcción manual
Comment comment = new Comment();
comment.setText(request.text());
comment.setPost(post);
comment.setAuthor(author);
```

`PostServiceImpl.createPost` usa `postMapper.toEntity(postRequest)`. `CommentServiceImpl` debería seguir el mismo patrón con `CommentMapper`.

---

### 5.3 Métodos huérfanos en `PostServiceImpl`

`getPostsByAuthor(String username)` y `getPostsByTitle(String title)` existen en la interfaz y la implementación pero **nunca se invocan** — `getAllPosts(author, title)` con Specifications los reemplaza completamente. Son dead code.

---

### 5.4 Mensaje de error en español — `CustomUserDetailService.java`

```java
throw new UsernameNotFoundException("Usuario no encontrado: " + username);
```

El resto del proyecto usa inglés. Normalizar el idioma.

---

### 5.5 Error semántico en `GlobalHandlerException.java:50-52`

`UnauthorizedException` devuelve HTTP 403 con campo `error: "Unauthorized"`. Semánticamente:
- **401 Unauthorized** = no autenticado.
- **403 Forbidden** = autenticado pero sin permisos.

Si la excepción representa "sin permisos", el código HTTP 403 es correcto pero el mensaje `"Unauthorized"` es engañoso. Cambiar a `"Forbidden"` o revisar el uso de la excepción.

---

### 5.6 Comentario TODO sin resolver — `UserServiceImpl.java:40`

```java
//TODO: enhance password encoding
newUser.setPassword(encoder.encode(userRequest.password()));
```

BCrypt con factor 10 (por defecto) es adecuado. El TODO es confuso — aclarar o eliminar.

---

### 5.7 Formato incorrecto en descripción Swagger — `OpenApiConfig.java`

La descripción del esquema de seguridad dice `"HTTP Basic"` pero la API usa Bearer JWT.

---

### 5.8 Código en misma línea — `UserRestController.java:59`

```java
UserResponse updatedUser = userService.updateUser(id, userRequest); return ResponseEntity.ok(updatedUser);
```

Dos sentencias en la misma línea. Separar.

---

## 6. MANEJO DE EXCEPCIONES — HANDLERS FALTANTES

`GlobalHandlerException` no tiene handlers para:

| Excepción                           | Cuándo ocurre                                          | Sin handler devuelve |
|-------------------------------------|--------------------------------------------------------|----------------------|
| `MethodArgumentNotValidException`   | `@Valid` falla (cuando se añada)                       | 400 formato Spring   |
| `HttpMessageNotReadableException`   | JSON malformado en el body                             | 400 formato Spring   |
| `DataIntegrityViolationException`   | Registro duplicado en DB (race condition en registro)  | 500                  |
| `MethodArgumentTypeMismatchException` | `/posts/abc` cuando id es Long                       | 400 formato Spring   |
| `Exception` (genérico)              | Cualquier error no anticipado                          | 500 con stack trace  |

Sin un handler genérico, excepciones no anticipadas pueden exponer stack traces con información interna.

---

## 7. RENDIMIENTO Y ESCALABILIDAD

### 7.1 Sin paginación en listados

`getAllPosts`, `getAllUsers` y `getCommentsByPostId` devuelven **todas** las filas sin paginación. Con datos reales esto puede colapsar la API.

**Solución:** Añadir `Pageable` como parámetro y devolver `Page<T>`.

---

### 7.2 FetchType.EAGER en `Post.author` — `Post.java`

```java
@ManyToOne(fetch = FetchType.EAGER)
private User author;
```

EAGER en `author` significa que cada consulta de Post hace un JOIN automático con `users`. Para `getAllPosts` con 1000 posts, esto es correcto (se necesita el autor para el response). Pero si en el futuro se añaden más relaciones EAGER, puede convertirse en un problema N+1.

---

### 7.3 Sin `@Transactional` en operaciones multi-paso

`CommentServiceImpl.createComment` hace múltiples queries de base de datos (existsById, findByUsername, save) sin transacción explícita. Si la JVM falla entre operaciones, puede dejar datos inconsistentes.

---

## 8. MEJORAS FUTURAS RECOMENDADAS

### 8.1 Separar UserDetails de la entidad User

Actualmente `User implements UserDetails`, acoplando el modelo de dominio con Spring Security. Lo recomendado es crear una clase `UserPrincipal` wrapper:

```java
public class UserPrincipal implements UserDetails {
    private final User user;
    // delega a user.*
}
```

Esto permite evolucionar la entidad sin afectar a Spring Security.

---

### 8.2 Perfiles Spring para configuración por entorno

Crear `application-dev.properties` y `application-prod.properties`:

```properties
# application-dev.properties
spring.jpa.hibernate.ddl-auto=create
spring.jpa.show-sql=true
logging.level.com.fnavas.blogengine=DEBUG

# application-prod.properties
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
logging.level.com.fnavas.blogengine=INFO
```

---

### 8.3 Refresh Tokens

El token JWT expira en 1 hora y no hay endpoint de renovación. El usuario tiene que hacer login de nuevo al expirar. Implementar un mecanismo de refresh token mejora la UX sin sacrificar seguridad.

---

### 8.4 Rate limiting en endpoints de autenticación

`/api/v1/auth/login` no tiene protección contra ataques de fuerza bruta. Añadir rate limiting (Bucket4j, Resilience4j) al menos en los endpoints de autenticación.

---

### 8.5 Volumen Docker para persistencia de datos

`docker-compose.yml` no tiene volumen para MySQL. Los datos se pierden al reiniciar el contenedor.

```yaml
volumes:
  - mysql_data:/var/lib/mysql
```

---

### 8.6 Externalizar duración del token JWT

```properties
app.jwt.expiration=3600000  # 1 hora en ms
```

Actualmente está hardcodeado como `1000 * 60 * 60 * 1` en `JwtService.java:25`.

---

### 8.7 Gestión de cuenta de usuario (campos en User)

`User` no tiene campos como `enabled`, `accountNonLocked`, etc. — todos los métodos `UserDetails` devuelven `true` hardcodeado. Para funcionalidad real (banear usuarios, verificación de email) se necesitarán estos campos en la entidad.

---

### 8.8 Tests de integración end-to-end

Actualmente los tests de controlador usan `@WebMvcTest` con mocks. Añadir tests `@SpringBootTest` con `MockMvc` que ejerciten el flujo completo (JWT real, base de datos H2) para cubrir la integración entre capas.

---

## 9. TABLA DE PRIORIDADES

| # | Problema | Archivo(s) | Severidad | Estado |
|---|----------|------------|-----------|--------|
| 1 | Import incorrecto ObjectMapper | `CustomAuthEntryPoint.java:11`, `CustomAccessDeniedHandler.java:12` | CRÍTICO | ✅ Resuelto |
| 2 | Inconsistencia signWith/getSigningKey JWT | `JwtService.java:26` | CRÍTICO | ✅ Resuelto |
| 3 | `@Valid` ausente en controladores | `*RestController.java` (todos) | ALTO | ✅ Resuelto |
| 4 | Formato de roles incorrecto en UserRestController | `UserRestController.java:54,63` | ALTO | ✅ Resuelto |
| 5 | Contraseñas en logs de debug | `UserServiceImpl.java:32`, `UserRestController.java:47` | ALTO | ✅ Resuelto |
| 6 | Clave JWT hardcodeada | `JwtService.java:18` | ALTO | ✅ Resuelto — externalizada a `application.yml` vía `${JWT_SECRET}` |
| 7 | DTOs sin validación | `UserRegisterRequest.java`, `CommentRequest.java` | ALTO | ✅ Resuelto |
| 8 | Handler genérico de excepciones ausente | `GlobalHandlerException.java` | MEDIO | ✅ Resuelto |
| 9 | `@ToString` expone password | `User.java:16` | MEDIO | ✅ Resuelto — `@ToString(exclude = "password")` |
| 10 | `setUpdatedAt` manual redundante | `UserServiceImpl.java:93` | BAJO | ✅ Resuelto |
| 11 | Constructor manual en CommentServiceImpl | `CommentServiceImpl.java:63` | BAJO | ✅ Resuelto — usa `CommentMapper.toEntity()` |
| 12 | Métodos huérfanos getPostsByAuthor/Title | `PostServiceImpl.java`, `PostService.java` | BAJO | ✅ Resuelto — reemplazados por `getAllPosts(author, title, pageable)` |
| 13 | Sin paginación en listados | `*ServiceImpl.java` (todos) | MEDIO | ✅ Resuelto — `Page<T>` con `@PageableDefault` en los 3 endpoints |
| 14 | Sin `@Transactional` en operaciones multi-paso | `*ServiceImpl.java` (todos) | MEDIO | ✅ Resuelto — `@Transactional` en mutaciones, `readOnly=true` en lecturas |
| 15 | Mensaje en español | `CustomUserDetailService.java` | BAJO | ✅ Resuelto |

---

## 10. PUNTOS FUERTES DEL PROYECTO

- Arquitectura en capas bien definida y consistente.
- Uso correcto de MapStruct para el mapeo entidad-DTO, incluyendo `toEntity` en todos los mappers.
- 65 tests cubriendo servicios, controladores, repositorios y beans de seguridad.
- Paginación completa con `Page<T>` y `@PageableDefault` en los tres endpoints de listado.
- `@Transactional` y `@Transactional(readOnly=true)` aplicados correctamente en toda la capa de servicio.
- Patrón de seguridad con beans `PostSecurity`, `UserSecurity`, `CommentSecurity` para `@PreAuthorize` es limpio y testeable.
- `@PreAuthorize` presente tanto en controladores (primer gate) como en servicios (ownership check).
- Logging estructurado con prefijos `[methodName]-Layer` en todos los servicios.
- Uso de records de Java para los DTOs (inmutabilidad).
- `@RestControllerAdvice` centraliza el manejo de errores correctamente.
- `BaseEntity` con auditoría (`createdAt`, `updatedAt`) aplicada a todas las entidades.
- Múltiples perfiles Spring (`dev`, `prod`, `test`) con credenciales externalizadas en producción.
