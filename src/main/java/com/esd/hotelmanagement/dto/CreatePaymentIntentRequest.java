package com.esd.hotelmanagement.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Request to create a Stripe payment intent.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentIntentRequest {

    @NotNull(message = "Reservation ID is required")
    private Long reservationId;

    /**
     * Currency code (defaults to "usd" if not specified).
     */
    private String currency;
}
