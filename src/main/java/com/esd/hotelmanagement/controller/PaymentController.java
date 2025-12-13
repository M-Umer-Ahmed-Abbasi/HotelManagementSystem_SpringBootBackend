package com.esd.hotelmanagement.controller;

import com.esd.hotelmanagement.dto.CreatePaymentIntentRequest;
import com.esd.hotelmanagement.dto.PaymentIntentResponse;
import com.esd.hotelmanagement.dto.PaymentRequest;
import com.esd.hotelmanagement.dto.PaymentResponse;
import com.esd.hotelmanagement.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

/**
 * REST controller for payment operations.
 * Supports both mock payments and Stripe integration.
 * 
 * @author Talha
 */
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Payment processing (Mock & Stripe)")
@SecurityRequirement(name = "bearerAuth")
public class PaymentController {

    private final PaymentService paymentService;

    // ==================== Mock Payment Endpoints ====================

    @PostMapping
    @Operation(summary = "Process a mock payment for a reservation (for testing)")
    public ResponseEntity<PaymentResponse> processPayment(@Valid @RequestBody PaymentRequest request) {
        PaymentResponse response = paymentService.processPayment(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // ==================== Stripe Payment Endpoints ====================

    @PostMapping("/stripe/create-intent")
    @Operation(summary = "Create a Stripe payment intent for a reservation")
    public ResponseEntity<PaymentIntentResponse> createPaymentIntent(
            @Valid @RequestBody CreatePaymentIntentRequest request) {
        PaymentIntentResponse response = paymentService.createPaymentIntent(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/stripe/confirm/{paymentIntentId}")
    @Operation(summary = "Confirm a Stripe payment after frontend completion")
    public ResponseEntity<PaymentResponse> confirmStripePayment(@PathVariable String paymentIntentId) {
        PaymentResponse response = paymentService.confirmStripePayment(paymentIntentId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/webhook")
    @Operation(summary = "Stripe webhook handler (called by Stripe)")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader(value = "Stripe-Signature", required = false) String sigHeader) {
        paymentService.handleStripeWebhook(payload, sigHeader);
        return ResponseEntity.ok("Received");
    }

    @GetMapping("/stripe/status")
    @Operation(summary = "Check if Stripe payments are enabled")
    public ResponseEntity<Map<String, Boolean>> getStripeStatus() {
        return ResponseEntity.ok(Map.of("enabled", paymentService.isStripeEnabled()));
    }

    // ==================== Common Endpoints ====================

    @GetMapping("/{id}")
    @Operation(summary = "Get payment by ID")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }

    @GetMapping("/reservation/{reservationId}")
    @Operation(summary = "Get payment for a reservation")
    public ResponseEntity<PaymentResponse> getPaymentByReservation(@PathVariable Long reservationId) {
        return ResponseEntity.ok(paymentService.getPaymentByReservation(reservationId));
    }

    @PostMapping("/{id}/refund")
    @Operation(summary = "Refund a payment (full)")
    public ResponseEntity<PaymentResponse> refundPayment(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.refundPayment(id));
    }

    @PostMapping("/{id}/refund/partial")
    @Operation(summary = "Refund a payment (partial)")
    public ResponseEntity<PaymentResponse> refundPaymentPartial(
            @PathVariable Long id,
            @RequestParam BigDecimal amount) {
        return ResponseEntity.ok(paymentService.refundPayment(id, amount));
    }
}
