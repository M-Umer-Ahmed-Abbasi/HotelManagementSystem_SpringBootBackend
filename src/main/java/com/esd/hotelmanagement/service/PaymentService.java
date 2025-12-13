package com.esd.hotelmanagement.service;

import com.esd.hotelmanagement.dto.*;
import com.esd.hotelmanagement.entity.*;
import com.esd.hotelmanagement.exception.BadRequestException;
import com.esd.hotelmanagement.exception.ResourceNotFoundException;
import com.esd.hotelmanagement.repository.PaymentRepository;
import com.esd.hotelmanagement.repository.ReservationRepository;
import com.esd.hotelmanagement.repository.TransactionLogRepository;
import com.stripe.model.PaymentIntent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Service for payment operations.
 * Supports both mock payments and Stripe integration.
 * 
 * @author Talha
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private final TransactionLogRepository transactionLogRepository;
    private final UserService userService;
    private final StripeService stripeService;

    // ==================== Mock Payment (Original) ====================

    /**
     * Process a mock payment (for testing or when Stripe is not configured).
     */
    @Transactional
    public PaymentResponse processPayment(PaymentRequest request) {
        Reservation reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", "id", request.getReservationId()));

        // Verify payment doesn't already exist
        if (paymentRepository.findByReservationId(reservation.getId()).isPresent()) {
            throw new BadRequestException("Payment already exists for this reservation");
        }

        // Verify amount matches reservation total
        if (request.getAmount().compareTo(reservation.getTotalPrice()) != 0) {
            throw new BadRequestException(
                    "Payment amount must match reservation total: " + reservation.getTotalPrice());
        }

        // Create payment
        Payment payment = Payment.builder()
                .reservation(reservation)
                .amount(request.getAmount())
                .paymentMethod(request.getPaymentMethod())
                .status(PaymentStatus.PENDING)
                .build();

        payment = paymentRepository.save(payment);

        // Log transaction
        logTransaction(payment, "PAYMENT_INITIATED", request.getAmount(), "Payment initiated");

        // Mock payment processing (always succeeds)
        String transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        payment.markAsSuccess(transactionId);
        payment = paymentRepository.save(payment);

        // Log success
        logTransaction(payment, "PAYMENT_SUCCESS", request.getAmount(), "Payment successful: " + transactionId);

        // Update reservation status
        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservationRepository.save(reservation);

        return mapToResponse(payment);
    }

    // ==================== Stripe Payment ====================

    /**
     * Create a Stripe payment intent for a reservation.
     * Returns client secret for frontend to complete payment.
     */
    @Transactional
    public PaymentIntentResponse createPaymentIntent(CreatePaymentIntentRequest request) {
        Reservation reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", "id", request.getReservationId()));

        // Check if Stripe is configured
        if (!stripeService.isEnabled()) {
            throw new BadRequestException("Stripe is not configured. Use mock payment endpoint instead.");
        }

        // Check if payment already exists
        if (paymentRepository.findByReservationId(reservation.getId()).isPresent()) {
            throw new BadRequestException("Payment already exists for this reservation");
        }

        String currency = request.getCurrency() != null ? request.getCurrency() : "usd";

        // Create Stripe payment intent
        PaymentIntentResponse intentResponse = stripeService.createPaymentIntent(reservation, currency);

        // Create pending payment record
        Payment payment = Payment.builder()
                .reservation(reservation)
                .amount(reservation.getTotalPrice())
                .paymentMethod("STRIPE")
                .status(PaymentStatus.PENDING)
                .stripePaymentIntentId(intentResponse.getPaymentIntentId())
                .currency(currency)
                .build();

        payment = paymentRepository.save(payment);

        // Log transaction
        logTransaction(payment, "STRIPE_INTENT_CREATED", reservation.getTotalPrice(),
                "Stripe PaymentIntent created: " + intentResponse.getPaymentIntentId());

        return intentResponse;
    }

    /**
     * Handle Stripe webhook events.
     * Called by PaymentController when Stripe sends webhook.
     */
    @Transactional
    public void handleStripeWebhook(String payload, String sigHeader) {
        // Note: In production, verify webhook signature using
        // stripeConfig.getWebhookSecret()
        // For now, we'll handle the basic flow
        log.info("Received Stripe webhook");

        // Parse the event and handle it
        // This is a simplified implementation. In production, use Stripe's Event
        // parsing.
        // The actual implementation would look like:
        // Event event = Webhook.constructEvent(payload, sigHeader,
        // stripeConfig.getWebhookSecret());
        // switch (event.getType()) {
        // case "payment_intent.succeeded" -> handlePaymentSuccess(event);
        // case "payment_intent.payment_failed" -> handlePaymentFailed(event);
        // }
    }

    /**
     * Confirm a Stripe payment after frontend completes it.
     * Called when we receive webhook or frontend confirms.
     */
    @Transactional
    public PaymentResponse confirmStripePayment(String paymentIntentId) {
        Payment payment = paymentRepository.findByStripePaymentIntentId(paymentIntentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "stripePaymentIntentId", paymentIntentId));

        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            return mapToResponse(payment);
        }

        // Retrieve payment intent from Stripe to verify status
        PaymentIntent intent = stripeService.retrievePaymentIntent(paymentIntentId);

        if ("succeeded".equals(intent.getStatus())) {
            payment.markAsSuccess(paymentIntentId);
            payment = paymentRepository.save(payment);

            // Update reservation status
            Reservation reservation = payment.getReservation();
            reservation.setStatus(ReservationStatus.CONFIRMED);
            reservationRepository.save(reservation);

            logTransaction(payment, "STRIPE_PAYMENT_SUCCESS", payment.getAmount(),
                    "Stripe payment confirmed: " + paymentIntentId);

            log.info("Confirmed Stripe payment for reservation {}", reservation.getId());
        } else if ("canceled".equals(intent.getStatus()) || "requires_payment_method".equals(intent.getStatus())) {
            payment.markAsFailed();
            paymentRepository.save(payment);
            logTransaction(payment, "STRIPE_PAYMENT_FAILED", payment.getAmount(),
                    "Stripe payment failed: " + intent.getStatus());
        }

        return mapToResponse(payment);
    }

    // ==================== Common Operations ====================

    public PaymentResponse getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", id));
        return mapToResponse(payment);
    }

    public PaymentResponse getPaymentByReservation(Long reservationId) {
        Payment payment = paymentRepository.findByReservationId(reservationId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Payment for reservation", "reservationId", reservationId));
        return mapToResponse(payment);
    }

    @Transactional
    public PaymentResponse refundPayment(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", id));

        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new BadRequestException("Only successful payments can be refunded");
        }

        // If it's a Stripe payment, refund via Stripe
        if (payment.getStripePaymentIntentId() != null && stripeService.isEnabled()) {
            stripeService.refundPayment(payment.getStripePaymentIntentId());
            log.info("Processed Stripe refund for payment {}", id);
        }

        payment.markAsRefunded();
        payment = paymentRepository.save(payment);

        // Log refund
        logTransaction(payment, "REFUND_SUCCESS", payment.getAmount(), "Full refund processed");

        return mapToResponse(payment);
    }

    @Transactional
    public PaymentResponse refundPayment(Long id, BigDecimal amount) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", id));

        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new BadRequestException("Only successful payments can be refunded");
        }

        if (amount.compareTo(payment.getAmount()) > 0) {
            throw new BadRequestException("Refund amount cannot exceed payment amount");
        }

        // If it's a Stripe payment, refund via Stripe
        if (payment.getStripePaymentIntentId() != null && stripeService.isEnabled()) {
            stripeService.refundPayment(payment.getStripePaymentIntentId(), amount);
            log.info("Processed Stripe partial refund for payment {}", id);
        }

        // For partial refunds, we still mark as refunded in this simplified
        // implementation
        payment.markAsRefunded();
        payment = paymentRepository.save(payment);

        // Log partial refund
        logTransaction(payment, "REFUND_SUCCESS", amount, "Partial refund: " + amount);

        return mapToResponse(payment);
    }

    /**
     * Check if Stripe payments are available.
     */
    public boolean isStripeEnabled() {
        return stripeService.isEnabled();
    }

    // ==================== Helper Methods ====================

    private void logTransaction(Payment payment, String action, BigDecimal amount, String description) {
        TransactionLog log = TransactionLog.builder()
                .payment(payment)
                .action(action)
                .amount(amount)
                .description(description)
                .build();
        transactionLogRepository.save(log);
    }

    private PaymentResponse mapToResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .status(payment.getStatus())
                .transactionId(payment.getTransactionId())
                .paidAt(payment.getPaidAt())
                .createdAt(payment.getCreatedAt())
                .reservationId(payment.getReservation().getId())
                .build();
    }
}
