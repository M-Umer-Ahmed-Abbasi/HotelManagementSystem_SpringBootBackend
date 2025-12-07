package com.esd.hotelmanagement.service;

import com.esd.hotelmanagement.dto.RevenueReportResponse;
import com.esd.hotelmanagement.entity.Hotel;
import com.esd.hotelmanagement.entity.ReservationStatus;
import com.esd.hotelmanagement.exception.ResourceNotFoundException;
import com.esd.hotelmanagement.repository.HotelRepository;
import com.esd.hotelmanagement.repository.PaymentRepository;
import com.esd.hotelmanagement.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Service for generating reports.
 * 
 * @author Talha
 */
@Service
@RequiredArgsConstructor
public class ReportService {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private final HotelRepository hotelRepository;

    public RevenueReportResponse getSystemWideReport(LocalDateTime startDate, LocalDateTime endDate) {
        BigDecimal revenue;
        if (startDate != null && endDate != null) {
            revenue = paymentRepository.calculateRevenueByDateRange(startDate, endDate);
        } else {
            revenue = paymentRepository.calculateTotalRevenue();
        }

        long totalBookings = reservationRepository.count();
        long confirmedBookings = reservationRepository.findByStatus(ReservationStatus.CONFIRMED).size()
                + reservationRepository.findByStatus(ReservationStatus.COMPLETED).size();
        long cancelledBookings = reservationRepository.findByStatus(ReservationStatus.CANCELLED).size();

        return RevenueReportResponse.builder()
                .totalRevenue(revenue)
                .totalBookings((int) totalBookings)
                .confirmedBookings((int) confirmedBookings)
                .cancelledBookings((int) cancelledBookings)
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }

    public RevenueReportResponse getHotelReport(Long hotelId, LocalDateTime startDate, LocalDateTime endDate) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel", "id", hotelId));

        BigDecimal revenue;
        if (startDate != null && endDate != null) {
            revenue = paymentRepository.calculateHotelRevenueByDateRange(hotelId, startDate, endDate);
        } else {
            revenue = paymentRepository.calculateHotelRevenue(hotelId);
        }

        var reservations = reservationRepository.findByHotelId(hotelId);
        int totalBookings = reservations.size();
        int confirmedBookings = (int) reservations.stream()
                .filter(r -> r.getStatus() == ReservationStatus.CONFIRMED
                        || r.getStatus() == ReservationStatus.COMPLETED)
                .count();
        int cancelledBookings = (int) reservations.stream()
                .filter(r -> r.getStatus() == ReservationStatus.CANCELLED)
                .count();

        return RevenueReportResponse.builder()
                .totalRevenue(revenue)
                .totalBookings(totalBookings)
                .confirmedBookings(confirmedBookings)
                .cancelledBookings(cancelledBookings)
                .startDate(startDate)
                .endDate(endDate)
                .hotelId(hotelId)
                .hotelName(hotel.getName())
                .build();
    }
}
