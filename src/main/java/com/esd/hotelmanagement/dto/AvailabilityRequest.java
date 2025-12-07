package com.esd.hotelmanagement.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request DTO for updating room availability.
 * 
 * @author Umer
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvailabilityRequest {

    @NotNull(message = "Date is required")
    private LocalDate date;

    private Boolean isAvailable = true;

    @Min(value = 0, message = "Price override must be non-negative")
    private BigDecimal priceOverride;
}
