package com.esd.hotelmanagement.repository;

import com.esd.hotelmanagement.entity.Availability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Availability entity.
 * 
 * @author Umer
 */
@Repository
public interface AvailabilityRepository extends JpaRepository<Availability, Long> {

    List<Availability> findByRoomId(Long roomId);

    List<Availability> findByRoomIdAndDateBetween(Long roomId, LocalDate startDate, LocalDate endDate);

    Optional<Availability> findByRoomIdAndDate(Long roomId, LocalDate date);

    @Query("SELECT a FROM Availability a WHERE a.room.id = :roomId AND a.date >= :startDate AND a.date < :endDate AND a.isAvailable = true")
    List<Availability> findAvailableDates(
            @Param("roomId") Long roomId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Modifying
    @Query("UPDATE Availability a SET a.isAvailable = :available WHERE a.room.id = :roomId AND a.date BETWEEN :startDate AND :endDate")
    int updateAvailability(
            @Param("roomId") Long roomId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("available") boolean available);
}
