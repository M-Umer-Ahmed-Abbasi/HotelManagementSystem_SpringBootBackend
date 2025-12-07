package com.esd.hotelmanagement.dto;

import lombok.*;

import java.time.LocalDateTime;

/**
 * Response DTO for review information.
 * 
 * @author Talha
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponse {

    private Long id;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
    private Long userId;
    private String userName;
    private Long reservationId;
    private Long hotelId;
    private String hotelName;
}
