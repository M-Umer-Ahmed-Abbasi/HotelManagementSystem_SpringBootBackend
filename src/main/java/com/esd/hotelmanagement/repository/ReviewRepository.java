package com.esd.hotelmanagement.repository;

import com.esd.hotelmanagement.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Review entity.
 * 
 * @author Talha
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByUserId(Long userId);

    Optional<Review> findByReservationId(Long reservationId);

    boolean existsByReservationId(Long reservationId);

    // Find reviews for a hotel
    @Query("SELECT r FROM Review r WHERE r.reservation.room.roomType.hotel.id = :hotelId ORDER BY r.createdAt DESC")
    List<Review> findByHotelId(@Param("hotelId") Long hotelId);

    // Calculate average rating for a hotel
    @Query("SELECT COALESCE(AVG(r.rating), 0) FROM Review r WHERE r.reservation.room.roomType.hotel.id = :hotelId")
    Double calculateAverageRating(@Param("hotelId") Long hotelId);
}
