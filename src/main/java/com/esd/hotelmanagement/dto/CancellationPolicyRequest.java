package com.esd.hotelmanagement.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Request DTO for cancellation policy.
 * 
 * @author Umer
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CancellationPolicyRequest {

    @NotBlank(message = "Policy name is required")
    private String name;

    private String description;

    @Min(0)
    private Integer daysBeforeCheckIn = 1;

    @Min(0)
    @Max(100)
    private Integer refundPercentage = 0;
}
