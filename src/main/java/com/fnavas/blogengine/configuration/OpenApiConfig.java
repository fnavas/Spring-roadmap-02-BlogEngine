package com.fnavas.blogengine.configuration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        return new OpenAPI()
                .info(new Info()
                        .title("Blog Engine REST API")
                        .version("1.0.0")
                        .description("""
                                RESTful Blog Engine API built with Spring Boot and secured with JWT authentication.

                                ## Authentication
                                1. Register a new user via `POST /api/v1/users`
                                2. Obtain a JWT token via `POST /api/v1/auth`
                                3. Click the **Authorize** button and enter: `Bearer <your-token>`

                                ## Authorization
                                - **Public**: Read posts, read users, register, login
                                - **Authenticated**: Create posts and comments
                                - **Owner/Admin**: Update and delete own resources
                                """)
                        .contact(new Contact()
                                .name("Fernando Navas")
                                .url("https://github.com/fnavas"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Enter your JWT token obtained from POST /api/v1/auth")));
    }
}
