package com.esd.hotelmanagement.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

/**
 * Application configuration using @Value annotation.
 * Demonstrates externalized configuration for application properties.
 * 
 * @author Generated
 */
@Configuration
@Getter
@Slf4j
public class AppConfig {

    @Value("${app.name:Hotel Management System}")
    private String appName;

    @Value("${app.version:1.0.0}")
    private String appVersion;

    @Value("${app.default-page-size:10}")
    private int defaultPageSize;

    @Value("${app.max-page-size:100}")
    private int maxPageSize;

    @Value("${server.port:8080}")
    private int serverPort;

    @Value("${spring.datasource.url}")
    private String databaseUrl;

    @Value("${jwt.expiration:86400000}")
    private long jwtExpiration;

    @PostConstruct
    public void logConfiguration() {
        log.info("===========================================");
        log.info("Application Configuration Loaded");
        log.info("===========================================");
        log.info("App Name: {}", appName);
        log.info("App Version: {}", appVersion);
        log.info("Server Port: {}", serverPort);
        log.info("Database URL: {}", databaseUrl);
        log.info("Default Page Size: {}", defaultPageSize);
        log.info("Max Page Size: {}", maxPageSize);
        log.info("JWT Expiration: {} ms", jwtExpiration);
        log.info("===========================================");
    }

    /**
     * Check if pagination size is within allowed limits.
     */
    public int validatePageSize(int requestedSize) {
        if (requestedSize <= 0) {
            return defaultPageSize;
        }
        return Math.min(requestedSize, maxPageSize);
    }
}
