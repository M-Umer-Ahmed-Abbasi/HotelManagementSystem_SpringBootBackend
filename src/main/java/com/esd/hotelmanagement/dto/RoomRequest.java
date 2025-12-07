package com.esd.hotelmanagement.dto;

import com.esd.hotelmanagement.entity.RoomStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Request DTO for creating/updating rooms.
 * 
 * @author Umer
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomRequest {

    @NotBlank(message = "Room number is required")
    private String roomNumber;

    private Integer floor;

    private RoomStatus status;
}
