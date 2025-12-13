package com.esd.hotelmanagement.dto;

import lombok.*;

/**
 * Response containing Stripe payment intent details.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentIntentResponse {

    /**
     * The client secret to complete payment on frontend.
     */
    private String clientSecret;

    /**
     * Stripe payment intent ID.
     */
    private String paymentIntentId;

    /**
     * Amount in cents.
     */
    private Long amount;

    /**
     * Currency code (e.g., "usd").
     */
    private String currency;

    /**
     * Current status of the payment intent.
     */
    private String status;

    /**
     * Associated reservation ID.
     */
    private Long reservationId;
}
