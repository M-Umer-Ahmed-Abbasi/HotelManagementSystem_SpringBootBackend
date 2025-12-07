package com.esd.hotelmanagement.repository;

import com.esd.hotelmanagement.entity.Payment;
import com.esd.hotelmanagement.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Payment entity.
 * 
 * @author Talha
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByReservationId(Long reservationId);

    List<Payment> findByStatus(PaymentStatus status);

    Optional<Payment> findByTransactionId(String transactionId);

    // Revenue calculations for admin reports
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = 'SUCCESS'")
    BigDecimal calculateTotalRevenue();

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = 'SUCCESS' AND p.paidAt BETWEEN :startDate AND :endDate")
    BigDecimal calculateRevenueByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // Revenue for a specific hotel
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p " +
            "WHERE p.status = 'SUCCESS' " +
            "AND p.reservation.room.roomType.hotel.id = :hotelId")
    BigDecimal calculateHotelRevenue(@Param("hotelId") Long hotelId);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p " +
            "WHERE p.status = 'SUCCESS' " +
            "AND p.reservation.room.roomType.hotel.id = :hotelId " +
            "AND p.paidAt BETWEEN :startDate AND :endDate")
    BigDecimal calculateHotelRevenueByDateRange(
            @Param("hotelId") Long hotelId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}
