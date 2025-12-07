package com.esd.hotelmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Request DTO for adding photos.
 * 
 * @author Talha
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhotoRequest {

    @NotBlank(message = "Photo URL is required")
    private String url;

    private String caption;

    private Integer displayOrder = 0;
}
