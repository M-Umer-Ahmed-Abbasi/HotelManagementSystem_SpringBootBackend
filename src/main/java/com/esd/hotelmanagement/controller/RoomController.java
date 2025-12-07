package com.esd.hotelmanagement.controller;

import com.esd.hotelmanagement.dto.*;
import com.esd.hotelmanagement.entity.RoomStatus;
import com.esd.hotelmanagement.service.RoomService;
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
 * REST controller for room and room type operations.
 * 
 * @author Umer
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Rooms", description = "Room and room type management")
@SecurityRequirement(name = "bearerAuth")
public class RoomController {

    private final RoomService roomService;

    // ==================== Room Types ====================

    @PostMapping("/hotels/{hotelId}/room-types")
    @Operation(summary = "Create room type for a hotel")
    public ResponseEntity<RoomTypeResponse> createRoomType(
            @PathVariable Long hotelId,
            @Valid @RequestBody RoomTypeRequest request) {
        RoomTypeResponse response = roomService.createRoomType(hotelId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/hotels/{hotelId}/room-types")
    @Operation(summary = "Get all room types for a hotel")
    public ResponseEntity<List<RoomTypeResponse>> getRoomTypesByHotel(@PathVariable Long hotelId) {
        return ResponseEntity.ok(roomService.getRoomTypesByHotel(hotelId));
    }

    @GetMapping("/room-types/{id}")
    @Operation(summary = "Get room type by ID")
    public ResponseEntity<RoomTypeResponse> getRoomTypeById(@PathVariable Long id) {
        return ResponseEntity.ok(roomService.getRoomTypeById(id));
    }

    @PutMapping("/room-types/{id}")
    @Operation(summary = "Update room type")
    public ResponseEntity<RoomTypeResponse> updateRoomType(
            @PathVariable Long id,
            @Valid @RequestBody RoomTypeRequest request) {
        return ResponseEntity.ok(roomService.updateRoomType(id, request));
    }

    @DeleteMapping("/room-types/{id}")
    @Operation(summary = "Delete room type")
    public ResponseEntity<Void> deleteRoomType(@PathVariable Long id) {
        roomService.deleteRoomType(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== Rooms ====================

    @PostMapping("/room-types/{roomTypeId}/rooms")
    @Operation(summary = "Create room for a room type")
    public ResponseEntity<RoomResponse> createRoom(
            @PathVariable Long roomTypeId,
            @Valid @RequestBody RoomRequest request) {
        RoomResponse response = roomService.createRoom(roomTypeId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/hotels/{hotelId}/rooms")
    @Operation(summary = "Get all rooms for a hotel")
    public ResponseEntity<List<RoomResponse>> getRoomsByHotel(@PathVariable Long hotelId) {
        return ResponseEntity.ok(roomService.getRoomsByHotel(hotelId));
    }

    @GetMapping("/room-types/{roomTypeId}/rooms")
    @Operation(summary = "Get rooms by room type")
    public ResponseEntity<List<RoomResponse>> getRoomsByRoomType(@PathVariable Long roomTypeId) {
        return ResponseEntity.ok(roomService.getRoomsByRoomType(roomTypeId));
    }

    @GetMapping("/rooms/{id}")
    @Operation(summary = "Get room by ID")
    public ResponseEntity<RoomResponse> getRoomById(@PathVariable Long id) {
        return ResponseEntity.ok(roomService.getRoomById(id));
    }

    @PutMapping("/rooms/{id}")
    @Operation(summary = "Update room")
    public ResponseEntity<RoomResponse> updateRoom(
            @PathVariable Long id,
            @Valid @RequestBody RoomRequest request) {
        return ResponseEntity.ok(roomService.updateRoom(id, request));
    }

    @DeleteMapping("/rooms/{id}")
    @Operation(summary = "Delete room")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/rooms/{id}/status")
    @Operation(summary = "Update room status")
    public ResponseEntity<RoomResponse> updateRoomStatus(
            @PathVariable Long id,
            @RequestParam RoomStatus status) {
        return ResponseEntity.ok(roomService.updateRoomStatus(id, status));
    }
}
