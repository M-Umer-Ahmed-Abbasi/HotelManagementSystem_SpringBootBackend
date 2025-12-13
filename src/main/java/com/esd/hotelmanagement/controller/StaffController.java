package com.esd.hotelmanagement.controller;

import com.esd.hotelmanagement.dto.StaffRequest;
import com.esd.hotelmanagement.dto.StaffResponse;
import com.esd.hotelmanagement.service.StaffService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for staff management.
 * 
 * @author Talha
 */
@RestController
@RequestMapping("/api/hotels/{hotelId}/staff")
@RequiredArgsConstructor
@Tag(name = "Staff", description = "Hotel staff management")
@SecurityRequirement(name = "bearerAuth")
public class StaffController {

    private final StaffService staffService;

    @PostMapping
    @Operation(summary = "Assign staff to hotel")
    public ResponseEntity<StaffResponse> assignStaff(
            @PathVariable Long hotelId,
            @Valid @RequestBody StaffRequest request) {
        StaffResponse response = staffService.assignStaff(hotelId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all staff for a hotel")
    public ResponseEntity<List<StaffResponse>> getStaffByHotel(@PathVariable Long hotelId) {
        return ResponseEntity.ok(staffService.getStaffByHotel(hotelId));
    }

    @GetMapping("/{staffId}")
    @Operation(summary = "Get staff by ID")
    public ResponseEntity<StaffResponse> getStaffById(
            @PathVariable Long hotelId,
            @PathVariable Long staffId) {
        return ResponseEntity.ok(staffService.getStaffById(staffId));
    }

    @PutMapping("/{staffId}")
    @Operation(summary = "Update staff")
    public ResponseEntity<StaffResponse> updateStaff(
            @PathVariable Long hotelId,
            @PathVariable Long staffId,
            @Valid @RequestBody StaffRequest request) {
        return ResponseEntity.ok(staffService.updateStaff(staffId, request));
    }

    @DeleteMapping("/{staffId}")
    @Operation(summary = "Remove staff from hotel")
    public ResponseEntity<Void> removeStaff(
            @PathVariable Long hotelId,
            @PathVariable Long staffId) {
        staffService.removeStaff(staffId);
        return ResponseEntity.noContent().build();
    }
}
