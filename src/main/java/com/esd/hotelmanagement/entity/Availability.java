package com.esd.hotelmanagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Availability entity for day-based room availability and pricing.
 * 
 * @author Umer
 */
@Entity
@Table(name = "availabilities", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "room_id", "date" })
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Availability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Date is required")
    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isAvailable = true;

    // Price override for this specific date (dynamic pricing)
    @Column(precision = 10, scale = 2)
    private BigDecimal priceOverride;

    // ==================== Relationship ====================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;
}
