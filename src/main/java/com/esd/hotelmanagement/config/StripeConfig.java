package com.esd.hotelmanagement.config;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Stripe payment gateway configuration.
 * Initializes the Stripe SDK with API keys from application properties.
 * 
 * @author Generated
 */
@Configuration
@Getter
@Slf4j
public class StripeConfig {

    @Value("${stripe.api-key}")
    private String apiKey;

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    @Value("${stripe.publishable-key}")
    private String publishableKey;

    @Value("${stripe.enabled:true}")
    private boolean enabled;

    @PostConstruct
    public void init() {
        if (enabled && apiKey != null && !apiKey.contains("placeholder")) {
            Stripe.apiKey = apiKey;
            log.info("Stripe SDK initialized successfully");
        } else {
            log.warn("Stripe is disabled or not configured. Using mock payments.");
        }
    }

    /**
     * Check if Stripe is properly configured and enabled.
     */
    public boolean isConfigured() {
        return enabled && apiKey != null && !apiKey.contains("placeholder");
    }
}
