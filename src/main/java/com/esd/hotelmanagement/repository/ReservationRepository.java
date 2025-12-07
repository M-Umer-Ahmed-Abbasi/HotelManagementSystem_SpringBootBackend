package com.esd.hotelmanagement.repository;

import com.esd.hotelmanagement.entity.Reservation;
import com.esd.hotelmanagement.entity.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository for Reservation entity.
 * 
 * @author Talha
 */
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByGuestId(Long guestId);

    Page<Reservation> findByGuestId(Long guestId, Pageable pageable);

    List<Reservation> findByGuestIdAndStatus(Long guestId, ReservationStatus status);

    List<Reservation> findByRoomId(Long roomId);

    List<Reservation> findByStatus(ReservationStatus status);

    // Find reservations for a room in a date range (for checking conflicts)
    @Query("SELECT r FROM Reservation r WHERE r.room.id = :roomId " +
            "AND r.status IN ('PENDING', 'CONFIRMED') " +
            "AND NOT (r.checkOutDate <= :checkIn OR r.checkInDate >= :checkOut)")
    List<Reservation> findConflictingReservations(
            @Param("roomId") Long roomId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut);

    // Find reservations for a hotel
    @Query("SELECT r FROM Reservation r WHERE r.room.roomType.hotel.id = :hotelId")
    List<Reservation> findByHotelId(@Param("hotelId") Long hotelId);

    @Query("SELECT r FROM Reservation r WHERE r.room.roomType.hotel.id = :hotelId")
    Page<Reservation> findByHotelId(@Param("hotelId") Long hotelId, Pageable pageable);

    // Find reservations by hotel owner
    @Query("SELECT r FROM Reservation r WHERE r.room.roomType.hotel.owner.id = :ownerId")
    List<Reservation> findByHotelOwnerId(@Param("ownerId") Long ownerId);
}
