package com.esd.hotelmanagement.dto;

import com.esd.hotelmanagement.entity.DiscountType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Response DTO for promotion information.
 * 
 * @author Talha
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromotionResponse {

    private Long id;
    private String code;
    private DiscountType discountType;
    private BigDecimal discountValue;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer usageLimit;
    private Integer usageCount;
    private Boolean enabled;
    private Boolean isValid;
}
