package com.esd.hotelmanagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Amenity entity representing hotel features/facilities.
 * 
 * @author Umer
 */
@Entity
@Table(name = "amenities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Amenity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Amenity name is required")
    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    private String icon;

    // ==================== Relationship ====================

    @ManyToMany(mappedBy = "amenities", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Hotel> hotels = new HashSet<>();
}
