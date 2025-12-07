package com.esd.hotelmanagement.service;

import com.esd.hotelmanagement.dto.PaymentRequest;
import com.esd.hotelmanagement.dto.PaymentResponse;
import com.esd.hotelmanagement.entity.*;
import com.esd.hotelmanagement.exception.BadRequestException;
import com.esd.hotelmanagement.exception.ResourceNotFoundException;
import com.esd.hotelmanagement.repository.PaymentRepository;
import com.esd.hotelmanagement.repository.ReservationRepository;
import com.esd.hotelmanagement.repository.TransactionLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Service for payment operations (mock implementation).
 * 
 * @author Talha
 */
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private final TransactionLogRepository transactionLogRepository;
    private final UserService userService;

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

        // For partial refunds, we still mark as refunded in this simplified
        // implementation
        payment.markAsRefunded();
        payment = paymentRepository.save(payment);

        // Log partial refund
        logTransaction(payment, "REFUND_SUCCESS", amount, "Partial refund: " + amount);

        return mapToResponse(payment);
    }

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
