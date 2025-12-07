package com.esd.hotelmanagement.controller;

import com.esd.hotelmanagement.dto.RevenueReportResponse;
import com.esd.hotelmanagement.dto.UserResponse;
import com.esd.hotelmanagement.entity.Role;
import com.esd.hotelmanagement.service.ReportService;
import com.esd.hotelmanagement.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REST controller for admin operations.
 * 
 * @author Talha
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Admin operations")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;
    private final ReportService reportService;

    @GetMapping("/users")
    @Operation(summary = "Get all users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/users/role/{role}")
    @Operation(summary = "Get users by role")
    public ResponseEntity<List<UserResponse>> getUsersByRole(@PathVariable Role role) {
        return ResponseEntity.ok(userService.getUsersByRole(role));
    }

    @PutMapping("/users/{id}/disable")
    @Operation(summary = "Disable a user")
    public ResponseEntity<Void> disableUser(@PathVariable Long id) {
        userService.disableUser(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/users/{id}/enable")
    @Operation(summary = "Enable a user")
    public ResponseEntity<Void> enableUser(@PathVariable Long id) {
        userService.enableUser(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/reports/revenue")
    @Operation(summary = "Get system-wide revenue report")
    public ResponseEntity<RevenueReportResponse> getSystemReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(reportService.getSystemWideReport(startDate, endDate));
    }

    @GetMapping("/reports/revenue/hotel/{hotelId}")
    @Operation(summary = "Get revenue report for a specific hotel")
    public ResponseEntity<RevenueReportResponse> getHotelReport(
            @PathVariable Long hotelId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(reportService.getHotelReport(hotelId, startDate, endDate));
    }
}
