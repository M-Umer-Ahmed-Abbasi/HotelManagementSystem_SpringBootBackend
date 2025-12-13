package com.esd.hotelmanagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Location entity for hotel addresses.
 * 
 * @author Umer
 */
@Entity
@Table(name = "locations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "City is required")
    @Column(nullable = false)
    private String city;

    private String state;

    @NotBlank(message = "Country is required")
    @Column(nullable = false)
    private String country;

    @NotBlank(message = "Address is required")
    @Column(nullable = false)
    private String address;

    private String zipCode;

    private Double latitude;

    private Double longitude;

    // ==================== Relationship ====================

    @OneToOne(mappedBy = "location")
    private Hotel hotel;
}
