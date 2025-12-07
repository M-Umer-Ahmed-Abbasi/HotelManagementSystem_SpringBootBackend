package com.esd.hotelmanagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Payment entity for reservation payments.
 * 
 * @author Talha
 */
@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Amount is required")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private String paymentMethod; // CREDIT_CARD, DEBIT_CARD, PAYPAL, etc.

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private PaymentStatus status = PaymentStatus.PENDING;

    private String transactionId; // External payment gateway transaction ID

    private LocalDateTime paidAt;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    // ==================== Relationships ====================

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false, unique = true)
    private Reservation reservation;

    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<TransactionLog> transactionLogs = new ArrayList<>();

    // ==================== Lifecycle ====================

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // ==================== Helper Methods ====================

    public void addTransactionLog(TransactionLog log) {
        transactionLogs.add(log);
        log.setPayment(this);
    }

    public void markAsSuccess(String txnId) {
        this.status = PaymentStatus.SUCCESS;
        this.transactionId = txnId;
        this.paidAt = LocalDateTime.now();
    }

    public void markAsFailed() {
        this.status = PaymentStatus.FAILED;
    }

    public void markAsRefunded() {
        this.status = PaymentStatus.REFUNDED;
    }
}
