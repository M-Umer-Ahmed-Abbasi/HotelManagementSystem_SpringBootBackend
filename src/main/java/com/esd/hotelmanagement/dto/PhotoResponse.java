package com.esd.hotelmanagement.dto;

import lombok.*;

/**
 * Response DTO for photo information.
 * 
 * @author Talha
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhotoResponse {

    private Long id;
    private String url;
    private String caption;
    private Integer displayOrder;
}
