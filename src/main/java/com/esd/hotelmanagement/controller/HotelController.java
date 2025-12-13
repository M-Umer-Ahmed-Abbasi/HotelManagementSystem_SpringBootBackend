package com.esd.hotelmanagement.controller;

import com.esd.hotelmanagement.dto.*;
import com.esd.hotelmanagement.service.HotelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * REST controller for hotel operations.
 * 
 * @author Umer
 */
@RestController
@RequestMapping("/api/hotels")
@RequiredArgsConstructor
@Tag(name = "Hotels", description = "Hotel management and search")
public class HotelController {

    private final HotelService hotelService;

    @PostMapping
    @Operation(summary = "Create a new hotel", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<HotelResponse> createHotel(@Valid @RequestBody HotelRequest request) {
        HotelResponse response = hotelService.createHotel(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Search hotels with filters", description = "Search for hotels with optional filters. For sorting, use valid fields like 'name', 'starRating', 'createdAt'. Leave sort empty for default ordering.")
    public ResponseEntity<Page<HotelResponse>> searchHotels(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Integer minRating,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(hotelService.searchHotels(city, minRating, minPrice, maxPrice, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get hotel by ID")
    public ResponseEntity<HotelResponse> getHotelById(@PathVariable Long id) {
        return ResponseEntity.ok(hotelService.getHotelById(id));
    }

    @GetMapping("/my-hotels")
    @Operation(summary = "Get current owner's hotels", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<HotelResponse>> getMyHotels() {
        return ResponseEntity.ok(hotelService.getMyHotels());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update hotel", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<HotelResponse> updateHotel(
            @PathVariable Long id,
            @Valid @RequestBody HotelRequest request) {
        return ResponseEntity.ok(hotelService.updateHotel(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete hotel", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> deleteHotel(@PathVariable Long id) {
        hotelService.deleteHotel(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/disable")
    @Operation(summary = "Disable hotel", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> disableHotel(@PathVariable Long id) {
        hotelService.disableHotel(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/enable")
    @Operation(summary = "Enable hotel", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> enableHotel(@PathVariable Long id) {
        hotelService.enableHotel(id);
        return ResponseEntity.ok().build();
    }
}
