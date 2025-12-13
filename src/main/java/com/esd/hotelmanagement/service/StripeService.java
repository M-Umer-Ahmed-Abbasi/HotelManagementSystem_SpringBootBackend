package com.esd.hotelmanagement.service;

import com.esd.hotelmanagement.config.StripeConfig;
import com.esd.hotelmanagement.dto.PaymentIntentResponse;
import com.esd.hotelmanagement.entity.Reservation;
import com.esd.hotelmanagement.exception.PaymentFailedException;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.RefundCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Service for Stripe payment operations.
 * Handles payment intent creation, confirmation, and refunds.
 * 
 * @author Generated
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StripeService {

    private final StripeConfig stripeConfig;

    /**
     * Create a Stripe PaymentIntent for a reservation.
     * 
     * @param reservation The reservation to create payment for
     * @param currency    Currency code (defaults to "usd")
     * @return PaymentIntentResponse with client secret
     */
    public PaymentIntentResponse createPaymentIntent(Reservation reservation, String currency) {
        if (!stripeConfig.isConfigured()) {
            throw new PaymentFailedException("Stripe is not configured. Please use mock payment.");
        }

        try {
            // Convert dollars to cents
            long amountInCents = reservation.getTotalPrice()
                    .multiply(BigDecimal.valueOf(100))
                    .longValue();

            String currencyCode = currency != null ? currency.toLowerCase() : "usd";

            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amountInCents)
                    .setCurrency(currencyCode)
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                    .setEnabled(true)
                                    .build())
                    .putMetadata("reservation_id", reservation.getId().toString())
                    .setDescription("Hotel Reservation #" + reservation.getId())
                    .build();

            PaymentIntent intent = PaymentIntent.create(params);

            log.info("Created PaymentIntent {} for reservation {}", intent.getId(), reservation.getId());

            return PaymentIntentResponse.builder()
                    .clientSecret(intent.getClientSecret())
                    .paymentIntentId(intent.getId())
                    .amount(amountInCents)
                    .currency(intent.getCurrency())
                    .status(intent.getStatus())
                    .reservationId(reservation.getId())
                    .build();
        } catch (StripeException e) {
            log.error("Stripe error creating payment intent: {}", e.getMessage());
            throw new PaymentFailedException("Failed to create payment intent: " + e.getMessage());
        }
    }

    /**
     * Retrieve a PaymentIntent by ID.
     */
    public PaymentIntent retrievePaymentIntent(String paymentIntentId) {
        if (!stripeConfig.isConfigured()) {
            throw new PaymentFailedException("Stripe is not configured.");
        }

        try {
            return PaymentIntent.retrieve(paymentIntentId);
        } catch (StripeException e) {
            log.error("Stripe error retrieving payment intent: {}", e.getMessage());
            throw new PaymentFailedException("Failed to retrieve payment intent: " + e.getMessage());
        }
    }

    /**
     * Create a full refund for a payment.
     */
    public Refund refundPayment(String paymentIntentId) {
        if (!stripeConfig.isConfigured()) {
            throw new PaymentFailedException("Stripe is not configured.");
        }

        try {
            RefundCreateParams params = RefundCreateParams.builder()
                    .setPaymentIntent(paymentIntentId)
                    .build();

            Refund refund = Refund.create(params);
            log.info("Created full refund {} for payment intent {}", refund.getId(), paymentIntentId);
            return refund;
        } catch (StripeException e) {
            log.error("Stripe refund error: {}", e.getMessage());
            throw new PaymentFailedException("Refund failed: " + e.getMessage());
        }
    }

    /**
     * Create a partial refund for a payment.
     * 
     * @param paymentIntentId Stripe payment intent ID
     * @param amount          Amount to refund in dollars
     */
    public Refund refundPayment(String paymentIntentId, BigDecimal amount) {
        if (!stripeConfig.isConfigured()) {
            throw new PaymentFailedException("Stripe is not configured.");
        }

        try {
            long amountInCents = amount.multiply(BigDecimal.valueOf(100)).longValue();

            RefundCreateParams params = RefundCreateParams.builder()
                    .setPaymentIntent(paymentIntentId)
                    .setAmount(amountInCents)
                    .build();

            Refund refund = Refund.create(params);
            log.info("Created partial refund {} for {} cents", refund.getId(), amountInCents);
            return refund;
        } catch (StripeException e) {
            log.error("Stripe partial refund error: {}", e.getMessage());
            throw new PaymentFailedException("Partial refund failed: " + e.getMessage());
        }
    }

    /**
     * Check if Stripe is properly configured.
     */
    public boolean isEnabled() {
        return stripeConfig.isConfigured();
    }
}
