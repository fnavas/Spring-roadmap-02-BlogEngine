package com.fnavas.BlogEngine.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. Deshabilitar CSRF ya que es una API (opcional pero recomendado para desarrollo)
                .csrf(csrf -> csrf.disable())

                // 2. Configurar reglas de acceso
                .authorizeHttpRequests(auth -> auth
                        // Permitimos el GET a la ruta base de posts
                        .requestMatchers("/api/v1/posts/**").permitAll()
                        // El resto de la aplicación requiere autenticación
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}