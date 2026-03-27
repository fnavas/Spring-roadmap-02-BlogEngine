package com.fnavas.blogengine.service;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    private static final String SECRET = "Z3VlbnRhX2VzdGFfY2xhdmVfc3VwZXJfc2VndXJhX3BhcmFfandrX2RlbW9fMjAyNg==";

    private JwtService jwtService;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", SECRET);
        userDetails = new User("testuser", "password", Collections.emptyList());
    }

    @Test
    void generateToken_returnsNonNullToken() {
        String token = jwtService.generateToken(userDetails);

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void generateToken_subjectMatchesUsername() {
        String token = jwtService.generateToken(userDetails);

        String extractedUsername = jwtService.extractUsername(token);

        assertEquals("testuser", extractedUsername);
    }

    @Test
    void extractUsername_validToken_returnsExpectedUsername() {
        String token = jwtService.generateToken(userDetails);

        String username = jwtService.extractUsername(token);

        assertEquals(userDetails.getUsername(), username);
    }

    @Test
    void isTokenValid_validTokenAndMatchingUser_returnsTrue() {
        String token = jwtService.generateToken(userDetails);

        boolean valid = jwtService.isTokenValid(token, userDetails);

        assertTrue(valid);
    }

    @Test
    void isTokenValid_tokenForDifferentUser_returnsFalse() {
        String token = jwtService.generateToken(userDetails);
        UserDetails otherUser = new User("otheruser", "password", Collections.emptyList());

        boolean valid = jwtService.isTokenValid(token, otherUser);

        assertFalse(valid);
    }

    @Test
    void isTokenValid_expiredToken_throwsExpiredJwtException() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        String expiredToken = Jwts.builder()
                .setSubject("testuser")
                .setIssuedAt(new Date(0))
                .setExpiration(new Date(0))
                .signWith(Keys.hmacShaKeyFor(keyBytes))
                .compact();

        assertThrows(ExpiredJwtException.class, () -> jwtService.isTokenValid(expiredToken, userDetails));
    }

    @Test
    void extractUsername_invalidToken_throwsException() {
        String invalidToken = "this.is.not.a.valid.jwt.token";

        assertThrows(JwtException.class, () -> jwtService.extractUsername(invalidToken));
    }
}
