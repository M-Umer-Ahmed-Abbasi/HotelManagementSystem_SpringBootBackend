package com.esd.hotelmanagement.controller;

import com.esd.hotelmanagement.dto.AvailabilityRequest;
import com.esd.hotelmanagement.dto.RoomResponse;
import com.esd.hotelmanagement.entity.Availability;
import com.esd.hotelmanagement.entity.Room;
import com.esd.hotelmanagement.service.AvailabilityService;
import com.esd.hotelmanagement.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for availability operations.
 * 
 * @author Umer
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Availability", description = "Room availability management")
public class AvailabilityController {

    private final AvailabilityService availabilityService;
    private final RoomService roomService;

    @GetMapping("/hotels/{hotelId}/availability")
    @Operation(summary = "Find available rooms for a hotel in date range")
    public ResponseEntity<List<RoomResponse>> getAvailableRooms(
            @PathVariable Long hotelId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut) {
        List<Room> rooms = availabilityService.findAvailableRooms(hotelId, checkIn, checkOut);
        List<RoomResponse> responses = rooms.stream()
                .map(room -> roomService.getRoomById(room.getId()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/rooms/{roomId}/availability")
    @Operation(summary = "Get availability for a room in date range")
    public ResponseEntity<List<Availability>> getRoomAvailability(
            @PathVariable Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(availabilityService.getAvailabilityByRoom(roomId, startDate, endDate));
    }

    @PutMapping("/rooms/{roomId}/availability")
    @Operation(summary = "Set availability for a room", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Availability> setAvailability(
            @PathVariable Long roomId,
            @Valid @RequestBody AvailabilityRequest request) {
        return ResponseEntity.ok(availabilityService.setAvailability(roomId, request));
    }

    @PutMapping("/rooms/{roomId}/availability/bulk")
    @Operation(summary = "Set availability for a room in date range", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> setAvailabilityBulk(
            @PathVariable Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam boolean available) {
        availabilityService.setAvailabilityBulk(roomId, startDate, endDate, available);
        return ResponseEntity.ok().build();
    }
}
