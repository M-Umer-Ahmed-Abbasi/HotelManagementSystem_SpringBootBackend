package com.esd.hotelmanagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * CancellationPolicy entity for hotel cancellation rules.
 * 
 * @author Talha
 */
@Entity
@Table(name = "cancellation_policies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CancellationPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Policy name is required")
    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    // Days before check-in for free cancellation
    @Min(0)
    @Column(nullable = false)
    @Builder.Default
    private Integer daysBeforeCheckIn = 1;

    // Refund percentage if cancelled after deadline
    @Min(0)
    @Max(100)
    @Column(nullable = false)
    @Builder.Default
    private Integer refundPercentage = 0;

    // ==================== Relationship ====================

    @OneToOne(mappedBy = "cancellationPolicy")
    private Hotel hotel;
}
