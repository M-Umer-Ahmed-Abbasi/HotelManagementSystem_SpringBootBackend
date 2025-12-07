package com.esd.hotelmanagement.dto;

import com.esd.hotelmanagement.entity.ReservationStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Response DTO for reservation information.
 * 
 * @author Talha
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationResponse {

    private Long id;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer guestCount;
    private BigDecimal totalPrice;
    private BigDecimal discountAmount;
    private String promoCode;
    private ReservationStatus status;
    private LocalDateTime createdAt;

    // Guest info
    private Long guestId;
    private String guestName;
    private String guestEmail;

    // Room info
    private Long roomId;
    private String roomNumber;
    private String roomTypeName;

    // Hotel info
    private Long hotelId;
    private String hotelName;
}
