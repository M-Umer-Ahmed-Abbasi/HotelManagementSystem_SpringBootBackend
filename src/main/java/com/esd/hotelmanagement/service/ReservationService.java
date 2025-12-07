package com.esd.hotelmanagement.service;

import com.esd.hotelmanagement.dto.ReservationRequest;
import com.esd.hotelmanagement.dto.ReservationResponse;
import com.esd.hotelmanagement.entity.*;
import com.esd.hotelmanagement.exception.BadRequestException;
import com.esd.hotelmanagement.exception.ResourceNotFoundException;
import com.esd.hotelmanagement.exception.UnauthorizedException;
import com.esd.hotelmanagement.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for reservation operations.
 * 
 * @author Talha
 */
@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final PromotionRepository promotionRepository;
    private final UserService userService;

    @Transactional
    public ReservationResponse createReservation(ReservationRequest request) {
        User guest = userService.getCurrentUser();

        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room", "id", request.getRoomId()));

        // Validate dates
        if (request.getCheckInDate().isBefore(LocalDate.now())) {
            throw new BadRequestException("Check-in date cannot be in the past");
        }
        if (request.getCheckOutDate().isBefore(request.getCheckInDate()) ||
                request.getCheckOutDate().equals(request.getCheckInDate())) {
            throw new BadRequestException("Check-out date must be after check-in date");
        }

        // Validate guest count
        if (request.getGuestCount() > room.getRoomType().getMaxOccupancy()) {
            throw new BadRequestException(
                    "Guest count exceeds room capacity of " + room.getRoomType().getMaxOccupancy());
        }

        // Check for conflicting reservations
        List<Reservation> conflicts = reservationRepository.findConflictingReservations(
                room.getId(), request.getCheckInDate(), request.getCheckOutDate());
        if (!conflicts.isEmpty()) {
            throw new BadRequestException("Room is not available for the selected dates");
        }

        // Calculate price
        long nights = ChronoUnit.DAYS.between(request.getCheckInDate(), request.getCheckOutDate());
        BigDecimal basePrice = room.getRoomType().getBasePrice();
        BigDecimal totalPrice = basePrice.multiply(BigDecimal.valueOf(nights));
        BigDecimal discountAmount = BigDecimal.ZERO;

        // Apply promo code if provided
        if (request.getPromoCode() != null && !request.getPromoCode().isBlank()) {
            Promotion promotion = promotionRepository.findByCodeIgnoreCase(request.getPromoCode())
                    .orElseThrow(() -> new BadRequestException("Invalid promo code"));
            if (!promotion.isValid()) {
                throw new BadRequestException("Promo code is expired or has reached its usage limit");
            }
            discountAmount = promotion.calculateDiscount(totalPrice);
            totalPrice = totalPrice.subtract(discountAmount);
            promotion.incrementUsage();
            promotionRepository.save(promotion);
        }

        Reservation reservation = Reservation.builder()
                .guest(guest)
                .room(room)
                .checkInDate(request.getCheckInDate())
                .checkOutDate(request.getCheckOutDate())
                .guestCount(request.getGuestCount())
                .totalPrice(totalPrice)
                .discountAmount(discountAmount)
                .promoCode(request.getPromoCode())
                .status(ReservationStatus.PENDING)
                .build();

        reservation = reservationRepository.save(reservation);
        return mapToResponse(reservation);
    }

    public ReservationResponse getReservationById(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", "id", id));
        return mapToResponse(reservation);
    }

    public List<ReservationResponse> getMyReservations() {
        User guest = userService.getCurrentUser();
        return reservationRepository.findByGuestId(guest.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<ReservationResponse> getReservationsByHotel(Long hotelId) {
        return reservationRepository.findByHotelId(hotelId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReservationResponse confirmReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", "id", id));

        checkOwnerPermission(reservation);

        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new BadRequestException("Only pending reservations can be confirmed");
        }

        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservation = reservationRepository.save(reservation);
        return mapToResponse(reservation);
    }

    @Transactional
    public ReservationResponse cancelReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", "id", id));

        User currentUser = userService.getCurrentUser();

        // Guest can cancel their own, Owner can cancel any in their hotel
        boolean isGuest = reservation.getGuest().getId().equals(currentUser.getId());
        boolean isOwner = reservation.getRoom().getRoomType().getHotel().getOwner().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;

        if (!isGuest && !isOwner && !isAdmin) {
            throw new UnauthorizedException("You don't have permission to cancel this reservation");
        }

        if (reservation.getStatus() == ReservationStatus.COMPLETED ||
                reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new BadRequestException("Reservation cannot be cancelled");
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservation = reservationRepository.save(reservation);
        return mapToResponse(reservation);
    }

    @Transactional
    public ReservationResponse completeReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", "id", id));

        checkOwnerPermission(reservation);

        reservation.setStatus(ReservationStatus.COMPLETED);
        reservation = reservationRepository.save(reservation);
        return mapToResponse(reservation);
    }

    private void checkOwnerPermission(Reservation reservation) {
        User currentUser = userService.getCurrentUser();
        Long ownerId = reservation.getRoom().getRoomType().getHotel().getOwner().getId();
        if (!ownerId.equals(currentUser.getId()) && currentUser.getRole() != Role.ADMIN) {
            throw new UnauthorizedException("You don't have permission to manage this reservation");
        }
    }

    private ReservationResponse mapToResponse(Reservation reservation) {
        Room room = reservation.getRoom();
        Hotel hotel = room.getRoomType().getHotel();
        User guest = reservation.getGuest();

        return ReservationResponse.builder()
                .id(reservation.getId())
                .checkInDate(reservation.getCheckInDate())
                .checkOutDate(reservation.getCheckOutDate())
                .guestCount(reservation.getGuestCount())
                .totalPrice(reservation.getTotalPrice())
                .discountAmount(reservation.getDiscountAmount())
                .promoCode(reservation.getPromoCode())
                .status(reservation.getStatus())
                .createdAt(reservation.getCreatedAt())
                .guestId(guest.getId())
                .guestName(guest.getFirstName() + " " + guest.getLastName())
                .guestEmail(guest.getEmail())
                .roomId(room.getId())
                .roomNumber(room.getRoomNumber())
                .roomTypeName(room.getRoomType().getName())
                .hotelId(hotel.getId())
                .hotelName(hotel.getName())
                .build();
    }
}
