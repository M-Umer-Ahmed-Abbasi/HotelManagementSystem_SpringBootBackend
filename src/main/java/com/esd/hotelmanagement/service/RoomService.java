package com.esd.hotelmanagement.service;

import com.esd.hotelmanagement.dto.*;
import com.esd.hotelmanagement.entity.*;
import com.esd.hotelmanagement.exception.ResourceNotFoundException;
import com.esd.hotelmanagement.exception.UnauthorizedException;
import com.esd.hotelmanagement.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for room and room type operations.
 * 
 * @author Umer
 */
@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final HotelRepository hotelRepository;
    private final UserService userService;

    @Transactional
    public RoomTypeResponse createRoomType(Long hotelId, RoomTypeRequest request) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel", "id", hotelId));

        checkHotelOwnership(hotel);

        RoomType roomType = RoomType.builder()
                .name(request.getName())
                .description(request.getDescription())
                .basePrice(request.getBasePrice())
                .maxOccupancy(request.getMaxOccupancy())
                .hotel(hotel)
                .build();

        roomType = roomTypeRepository.save(roomType);
        return mapRoomTypeToResponse(roomType);
    }

    public List<RoomTypeResponse> getRoomTypesByHotel(Long hotelId) {
        return roomTypeRepository.findByHotelId(hotelId).stream()
                .map(this::mapRoomTypeToResponse)
                .collect(Collectors.toList());
    }

    public RoomTypeResponse getRoomTypeById(Long id) {
        RoomType roomType = roomTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RoomType", "id", id));
        return mapRoomTypeToResponse(roomType);
    }

    @Transactional
    public RoomTypeResponse updateRoomType(Long id, RoomTypeRequest request) {
        RoomType roomType = roomTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RoomType", "id", id));

        checkHotelOwnership(roomType.getHotel());

        roomType.setName(request.getName());
        roomType.setDescription(request.getDescription());
        roomType.setBasePrice(request.getBasePrice());
        roomType.setMaxOccupancy(request.getMaxOccupancy());

        roomType = roomTypeRepository.save(roomType);
        return mapRoomTypeToResponse(roomType);
    }

    @Transactional
    public void deleteRoomType(Long id) {
        RoomType roomType = roomTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RoomType", "id", id));
        checkHotelOwnership(roomType.getHotel());
        roomTypeRepository.delete(roomType);
    }

    @Transactional
    public RoomResponse createRoom(Long roomTypeId, RoomRequest request) {
        RoomType roomType = roomTypeRepository.findById(roomTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("RoomType", "id", roomTypeId));

        checkHotelOwnership(roomType.getHotel());

        Room room = Room.builder()
                .roomNumber(request.getRoomNumber())
                .floor(request.getFloor())
                .status(request.getStatus() != null ? request.getStatus() : RoomStatus.AVAILABLE)
                .roomType(roomType)
                .build();

        room = roomRepository.save(room);
        return mapRoomToResponse(room);
    }

    public List<RoomResponse> getRoomsByHotel(Long hotelId) {
        return roomRepository.findByRoomTypeHotelId(hotelId).stream()
                .map(this::mapRoomToResponse)
                .collect(Collectors.toList());
    }

    public List<RoomResponse> getRoomsByRoomType(Long roomTypeId) {
        return roomRepository.findByRoomTypeId(roomTypeId).stream()
                .map(this::mapRoomToResponse)
                .collect(Collectors.toList());
    }

    public RoomResponse getRoomById(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room", "id", id));
        return mapRoomToResponse(room);
    }

    @Transactional
    public RoomResponse updateRoom(Long id, RoomRequest request) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room", "id", id));

        checkHotelOwnership(room.getRoomType().getHotel());

        room.setRoomNumber(request.getRoomNumber());
        room.setFloor(request.getFloor());
        if (request.getStatus() != null) {
            room.setStatus(request.getStatus());
        }

        room = roomRepository.save(room);
        return mapRoomToResponse(room);
    }

    @Transactional
    public void deleteRoom(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room", "id", id));
        checkHotelOwnership(room.getRoomType().getHotel());
        roomRepository.delete(room);
    }

    @Transactional
    public RoomResponse updateRoomStatus(Long id, RoomStatus status) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room", "id", id));
        room.setStatus(status);
        room = roomRepository.save(room);
        return mapRoomToResponse(room);
    }

    private void checkHotelOwnership(Hotel hotel) {
        User currentUser = userService.getCurrentUser();
        if (!hotel.getOwner().getId().equals(currentUser.getId())
                && currentUser.getRole() != Role.ADMIN
                && currentUser.getRole() != Role.STAFF) {
            throw new UnauthorizedException("You don't have permission to manage this hotel's rooms");
        }
    }

    private RoomTypeResponse mapRoomTypeToResponse(RoomType roomType) {
        long availableCount = roomType.getRooms().stream()
                .filter(r -> r.getStatus() == RoomStatus.AVAILABLE)
                .count();

        return RoomTypeResponse.builder()
                .id(roomType.getId())
                .name(roomType.getName())
                .description(roomType.getDescription())
                .basePrice(roomType.getBasePrice())
                .maxOccupancy(roomType.getMaxOccupancy())
                .hotelId(roomType.getHotel().getId())
                .hotelName(roomType.getHotel().getName())
                .roomCount(roomType.getRooms().size())
                .availableRoomCount((int) availableCount)
                .build();
    }

    private RoomResponse mapRoomToResponse(Room room) {
        RoomType roomType = room.getRoomType();
        return RoomResponse.builder()
                .id(room.getId())
                .roomNumber(room.getRoomNumber())
                .floor(room.getFloor())
                .status(room.getStatus())
                .roomTypeId(roomType.getId())
                .roomTypeName(roomType.getName())
                .basePrice(roomType.getBasePrice())
                .maxOccupancy(roomType.getMaxOccupancy())
                .hotelId(roomType.getHotel().getId())
                .hotelName(roomType.getHotel().getName())
                .build();
    }
}
