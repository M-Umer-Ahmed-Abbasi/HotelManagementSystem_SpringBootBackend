package com.esd.hotelmanagement.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response DTO for revenue reports.
 * 
 * @author Talha
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RevenueReportResponse {

    private BigDecimal totalRevenue;
    private Integer totalBookings;
    private Integer confirmedBookings;
    private Integer cancelledBookings;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    // For hotel-specific reports
    private Long hotelId;
    private String hotelName;
}
