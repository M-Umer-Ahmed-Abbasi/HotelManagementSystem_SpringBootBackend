package com.esd.hotelmanagement.service;

import com.esd.hotelmanagement.dto.AvailabilityRequest;
import com.esd.hotelmanagement.entity.Availability;
import com.esd.hotelmanagement.entity.Room;
import com.esd.hotelmanagement.exception.ResourceNotFoundException;
import com.esd.hotelmanagement.repository.AvailabilityRepository;
import com.esd.hotelmanagement.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Service for room availability operations.
 * 
 * @author Umer
 */
@Service
@RequiredArgsConstructor
public class AvailabilityService {

    private final AvailabilityRepository availabilityRepository;
    private final RoomRepository roomRepository;

    public List<Availability> getAvailabilityByRoom(Long roomId, LocalDate startDate, LocalDate endDate) {
        return availabilityRepository.findByRoomIdAndDateBetween(roomId, startDate, endDate);
    }

    @Transactional
    public Availability setAvailability(Long roomId, AvailabilityRequest request) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room", "id", roomId));

        Availability availability = availabilityRepository.findByRoomIdAndDate(roomId, request.getDate())
                .orElse(Availability.builder()
                        .room(room)
                        .date(request.getDate())
                        .build());

        availability.setIsAvailable(request.getIsAvailable());
        availability.setPriceOverride(request.getPriceOverride());

        return availabilityRepository.save(availability);
    }

    @Transactional
    public void setAvailabilityBulk(Long roomId, LocalDate startDate, LocalDate endDate, boolean isAvailable) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room", "id", roomId));

        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            final LocalDate date = currentDate;
            Availability availability = availabilityRepository.findByRoomIdAndDate(roomId, date)
                    .orElse(Availability.builder()
                            .room(room)
                            .date(date)
                            .build());
            availability.setIsAvailable(isAvailable);
            availabilityRepository.save(availability);
            currentDate = currentDate.plusDays(1);
        }
    }

    public List<Room> findAvailableRooms(Long hotelId, LocalDate checkIn, LocalDate checkOut) {
        return roomRepository.findAvailableRooms(hotelId, checkIn, checkOut);
    }
}
