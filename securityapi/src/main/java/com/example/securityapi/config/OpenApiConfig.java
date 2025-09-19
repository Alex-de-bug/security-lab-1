package com.example.securityapi.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(
            new Info()
                .title("Secure REST API")
                .version("1.0.0")
                .description(
                    "Защищенный REST API с JWT аутентификацией, созданный с использованием Spring"
                        + " Boot и Spring Security")
                .contact(
                    new Contact()
                        .name("API Support")
                        .email("support@example.com")
                        .url("https://example.com"))
                .license(
                    new License().name("MIT License").url("https://opensource.org/licenses/MIT")))
        .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
        .components(
            new Components()
                .addSecuritySchemes(
                    "Bearer Authentication",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("Введите JWT токен в формате: Bearer <token>")));
  }
}
