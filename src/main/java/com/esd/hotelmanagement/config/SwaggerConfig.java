package com.esd.hotelmanagement.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI/Swagger configuration.
 * 
 * @author Umer
 */
@Configuration
@OpenAPIDefinition(info = @Info(title = "Hotel Management System API", version = "1.0", description = "REST API for Hotel Management System - A complete backend for hotel booking platform", contact = @Contact(name = "ESD Project Team", email = "m.umer.ahmed.abbasi@gmail.com")))
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT", description = "JWT authentication. Get token from /api/auth/login")
public class SwaggerConfig {
}
