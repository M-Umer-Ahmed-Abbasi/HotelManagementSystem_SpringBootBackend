package com.esd.hotelmanagement.repository;

import com.esd.hotelmanagement.entity.Room;
import com.esd.hotelmanagement.entity.RoomStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository for Room entity.
 * 
 * @author Umer
 */
@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findByRoomTypeId(Long roomTypeId);

    List<Room> findByRoomTypeHotelId(Long hotelId);

    List<Room> findByStatus(RoomStatus status);

    List<Room> findByRoomTypeIdAndStatus(Long roomTypeId, RoomStatus status);

    // Find available rooms for a hotel within date range
    @Query("SELECT DISTINCT r FROM Room r " +
            "JOIN r.roomType rt " +
            "WHERE rt.hotel.id = :hotelId " +
            "AND r.status = 'AVAILABLE' " +
            "AND NOT EXISTS (" +
            "  SELECT 1 FROM Availability a " +
            "  WHERE a.room = r " +
            "  AND a.date BETWEEN :checkIn AND :checkOut " +
            "  AND a.isAvailable = false" +
            ") " +
            "AND NOT EXISTS (" +
            "  SELECT 1 FROM Reservation res " +
            "  WHERE res.room = r " +
            "  AND res.status IN ('PENDING', 'CONFIRMED') " +
            "  AND NOT (res.checkOutDate <= :checkIn OR res.checkInDate >= :checkOut)" +
            ")")
    List<Room> findAvailableRooms(
            @Param("hotelId") Long hotelId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut);
}
