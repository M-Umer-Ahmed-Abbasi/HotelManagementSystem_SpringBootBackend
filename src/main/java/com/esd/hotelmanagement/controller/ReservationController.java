package com.esd.hotelmanagement.controller;

import com.esd.hotelmanagement.dto.ReservationRequest;
import com.esd.hotelmanagement.dto.ReservationResponse;
import com.esd.hotelmanagement.service.ReservationService;
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
 * REST controller for reservation operations.
 * 
 * @author Talha
 */
@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@Tag(name = "Reservations", description = "Booking management")
@SecurityRequirement(name = "bearerAuth")
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    @Operation(summary = "Create a new reservation")
    public ResponseEntity<ReservationResponse> createReservation(@Valid @RequestBody ReservationRequest request) {
        ReservationResponse response = reservationService.createReservation(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get reservation by ID")
    public ResponseEntity<ReservationResponse> getReservationById(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.getReservationById(id));
    }

    @GetMapping
    @Operation(summary = "Get current user's reservations")
    public ResponseEntity<List<ReservationResponse>> getMyReservations() {
        return ResponseEntity.ok(reservationService.getMyReservations());
    }

    @GetMapping("/hotel/{hotelId}")
    @Operation(summary = "Get reservations for a hotel (owner only)")
    public ResponseEntity<List<ReservationResponse>> getReservationsByHotel(@PathVariable Long hotelId) {
        return ResponseEntity.ok(reservationService.getReservationsByHotel(hotelId));
    }

    @PutMapping("/{id}/confirm")
    @Operation(summary = "Confirm a reservation (owner only)")
    public ResponseEntity<ReservationResponse> confirmReservation(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.confirmReservation(id));
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "Cancel a reservation")
    public ResponseEntity<ReservationResponse> cancelReservation(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.cancelReservation(id));
    }

    @PutMapping("/{id}/complete")
    @Operation(summary = "Mark reservation as completed (owner only)")
    public ResponseEntity<ReservationResponse> completeReservation(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.completeReservation(id));
    }
}
