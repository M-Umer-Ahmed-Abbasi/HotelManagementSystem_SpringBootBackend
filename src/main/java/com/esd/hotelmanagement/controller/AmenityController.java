package com.esd.hotelmanagement.controller;

import com.esd.hotelmanagement.dto.AmenityRequest;
import com.esd.hotelmanagement.dto.AmenityResponse;
import com.esd.hotelmanagement.service.AmenityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for amenity operations.
 * 
 * @author Umer
 */
@RestController
@RequestMapping("/api/amenities")
@RequiredArgsConstructor
@Tag(name = "Amenities", description = "Hotel amenities management")
public class AmenityController {

    private final AmenityService amenityService;

    @GetMapping
    @Operation(summary = "Get all amenities")
    public ResponseEntity<List<AmenityResponse>> getAllAmenities() {
        return ResponseEntity.ok(amenityService.getAllAmenities());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get amenity by ID")
    public ResponseEntity<AmenityResponse> getAmenityById(@PathVariable Long id) {
        return ResponseEntity.ok(amenityService.getAmenityById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new amenity (Admin only)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<AmenityResponse> createAmenity(@Valid @RequestBody AmenityRequest request) {
        AmenityResponse response = amenityService.createAmenity(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update amenity (Admin only)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<AmenityResponse> updateAmenity(
            @PathVariable Long id,
            @Valid @RequestBody AmenityRequest request) {
        return ResponseEntity.ok(amenityService.updateAmenity(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete amenity (Admin only)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> deleteAmenity(@PathVariable Long id) {
        amenityService.deleteAmenity(id);
        return ResponseEntity.noContent().build();
    }
}
