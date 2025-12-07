package com.esd.hotelmanagement.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

/**
 * Request DTO for creating/updating room types.
 * 
 * @author Umer
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomTypeRequest {

    @NotBlank(message = "Room type name is required")
    private String name;

    private String description;

    @NotNull(message = "Base price is required")
    @Min(value = 0, message = "Base price must be non-negative")
    private BigDecimal basePrice;

    @NotNull(message = "Max occupancy is required")
    @Min(value = 1, message = "Max occupancy must be at least 1")
    private Integer maxOccupancy;
}
