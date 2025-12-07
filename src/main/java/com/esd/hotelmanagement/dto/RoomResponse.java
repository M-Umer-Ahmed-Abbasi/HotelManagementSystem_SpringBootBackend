package com.esd.hotelmanagement.dto;

import com.esd.hotelmanagement.entity.RoomStatus;
import lombok.*;

import java.math.BigDecimal;

/**
 * Response DTO for room information.
 * 
 * @author Umer
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomResponse {

    private Long id;
    private String roomNumber;
    private Integer floor;
    private RoomStatus status;
    private Long roomTypeId;
    private String roomTypeName;
    private BigDecimal basePrice;
    private Integer maxOccupancy;
    private Long hotelId;
    private String hotelName;
}
