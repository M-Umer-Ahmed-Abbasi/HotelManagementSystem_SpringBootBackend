package com.esd.hotelmanagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Promotion entity for discount codes.
 * 
 * @author Talha
 */
@Entity
@Table(name = "promotions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Promo code is required")
    @Column(nullable = false, unique = true)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiscountType discountType;

    @NotNull(message = "Discount value is required")
    @Min(value = 0, message = "Discount value must be non-negative")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal discountValue;

    @NotNull(message = "Start date is required")
    @Column(nullable = false)
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    @Column(nullable = false)
    private LocalDate endDate;

    @Min(value = 0, message = "Usage limit must be non-negative")
    private Integer usageLimit;

    @Column(nullable = false)
    @Builder.Default
    private Integer usageCount = 0;

    @Column(nullable = false)
    @Builder.Default
    private Boolean enabled = true;

    // ==================== Helper Methods ====================

    public boolean isValid() {
        LocalDate today = LocalDate.now();
        boolean withinDateRange = !today.isBefore(startDate) && !today.isAfter(endDate);
        boolean withinUsageLimit = usageLimit == null || usageCount < usageLimit;
        return enabled && withinDateRange && withinUsageLimit;
    }

    public void incrementUsage() {
        this.usageCount++;
    }

    public BigDecimal calculateDiscount(BigDecimal originalAmount) {
        if (discountType == DiscountType.PERCENTAGE) {
            return originalAmount.multiply(discountValue).divide(BigDecimal.valueOf(100));
        } else {
            return discountValue.min(originalAmount);
        }
    }
}
