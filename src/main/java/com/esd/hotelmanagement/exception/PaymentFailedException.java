package com.esd.hotelmanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when payment processing fails.
 * 
 * @author Talha
 */
@ResponseStatus(HttpStatus.PAYMENT_REQUIRED)
public class PaymentFailedException extends RuntimeException {

    public PaymentFailedException(String message) {
        super(message);
    }
}
