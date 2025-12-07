package com.esd.hotelmanagement.service;

import com.esd.hotelmanagement.dto.*;
import com.esd.hotelmanagement.entity.*;
import com.esd.hotelmanagement.exception.ResourceNotFoundException;
import com.esd.hotelmanagement.exception.UnauthorizedException;
import com.esd.hotelmanagement.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for hotel operations.
 * 
 * @author Umer
 */
@Service
@RequiredArgsConstructor
public class HotelService {

    private final HotelRepository hotelRepository;
    private final AmenityRepository amenityRepository;
    private final ReviewRepository reviewRepository;
    private final UserService userService;

    @Transactional
    public HotelResponse createHotel(HotelRequest request) {
        User owner = userService.getCurrentUser();

        // Only OWNER and ADMIN can create hotels
        if (owner.getRole() != Role.OWNER && owner.getRole() != Role.ADMIN) {
            throw new UnauthorizedException("Only owners can create hotels");
        }

        Hotel hotel = Hotel.builder()
                .name(request.getName())
                .description(request.getDescription())
                .starRating(request.getStarRating())
                .owner(owner)
                .enabled(true)
                .build();

        // Set location
        if (request.getLocation() != null) {
            Location location = Location.builder()
                    .city(request.getLocation().getCity())
                    .state(request.getLocation().getState())
                    .country(request.getLocation().getCountry())
                    .address(request.getLocation().getAddress())
                    .zipCode(request.getLocation().getZipCode())
                    .latitude(request.getLocation().getLatitude())
                    .longitude(request.getLocation().getLongitude())
                    .build();
            hotel.setLocation(location);
        }

        // Set cancellation policy
        if (request.getCancellationPolicy() != null) {
            CancellationPolicy policy = CancellationPolicy.builder()
                    .name(request.getCancellationPolicy().getName())
                    .description(request.getCancellationPolicy().getDescription())
                    .daysBeforeCheckIn(request.getCancellationPolicy().getDaysBeforeCheckIn())
                    .refundPercentage(request.getCancellationPolicy().getRefundPercentage())
                    .build();
            hotel.setCancellationPolicy(policy);
        }

        // Add amenities
        if (request.getAmenityIds() != null && !request.getAmenityIds().isEmpty()) {
            for (Long amenityId : request.getAmenityIds()) {
                Amenity amenity = amenityRepository.findById(amenityId)
                        .orElseThrow(() -> new ResourceNotFoundException("Amenity", "id", amenityId));
                hotel.addAmenity(amenity);
            }
        }

        hotel = hotelRepository.save(hotel);
        return mapToResponse(hotel);
    }

    public HotelResponse getHotelById(Long id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel", "id", id));
        return mapToResponse(hotel);
    }

    public Page<HotelResponse> searchHotels(String city, Integer minRating, BigDecimal minPrice, BigDecimal maxPrice,
            Pageable pageable) {
        return hotelRepository.searchHotels(city, minRating, minPrice, maxPrice, pageable)
                .map(this::mapToResponse);
    }

    public List<HotelResponse> getHotelsByOwner(Long ownerId) {
        return hotelRepository.findByOwnerId(ownerId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<HotelResponse> getMyHotels() {
        User owner = userService.getCurrentUser();
        return hotelRepository.findByOwner(owner).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public HotelResponse updateHotel(Long id, HotelRequest request) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel", "id", id));

        User currentUser = userService.getCurrentUser();

        // Check ownership
        if (!hotel.getOwner().getId().equals(currentUser.getId()) && currentUser.getRole() != Role.ADMIN) {
            throw new UnauthorizedException("You can only update your own hotels");
        }

        hotel.setName(request.getName());
        hotel.setDescription(request.getDescription());
        hotel.setStarRating(request.getStarRating());

        // Update location
        if (request.getLocation() != null) {
            Location location = hotel.getLocation();
            if (location == null) {
                location = new Location();
                hotel.setLocation(location);
            }
            location.setCity(request.getLocation().getCity());
            location.setState(request.getLocation().getState());
            location.setCountry(request.getLocation().getCountry());
            location.setAddress(request.getLocation().getAddress());
            location.setZipCode(request.getLocation().getZipCode());
            location.setLatitude(request.getLocation().getLatitude());
            location.setLongitude(request.getLocation().getLongitude());
        }

        hotel = hotelRepository.save(hotel);
        return mapToResponse(hotel);
    }

    @Transactional
    public void deleteHotel(Long id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel", "id", id));

        User currentUser = userService.getCurrentUser();

        if (!hotel.getOwner().getId().equals(currentUser.getId()) && currentUser.getRole() != Role.ADMIN) {
            throw new UnauthorizedException("You can only delete your own hotels");
        }

        hotelRepository.delete(hotel);
    }

    @Transactional
    public void disableHotel(Long id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel", "id", id));
        hotel.setEnabled(false);
        hotelRepository.save(hotel);
    }

    @Transactional
    public void enableHotel(Long id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel", "id", id));
        hotel.setEnabled(true);
        hotelRepository.save(hotel);
    }

    private HotelResponse mapToResponse(Hotel hotel) {
        Double avgRating = reviewRepository.calculateAverageRating(hotel.getId());
        List<Review> reviews = reviewRepository.findByHotelId(hotel.getId());

        return HotelResponse.builder()
                .id(hotel.getId())
                .name(hotel.getName())
                .description(hotel.getDescription())
                .starRating(hotel.getStarRating())
                .enabled(hotel.getEnabled())
                .createdAt(hotel.getCreatedAt())
                .ownerId(hotel.getOwner().getId())
                .ownerName(hotel.getOwner().getFirstName() + " " + hotel.getOwner().getLastName())
                .location(hotel.getLocation() != null ? mapLocationToResponse(hotel.getLocation()) : null)
                .amenities(hotel.getAmenities().stream()
                        .map(this::mapAmenityToResponse)
                        .collect(Collectors.toList()))
                .photos(hotel.getPhotos().stream()
                        .map(this::mapPhotoToResponse)
                        .collect(Collectors.toList()))
                .averageRating(avgRating)
                .reviewCount(reviews.size())
                .roomTypeCount(hotel.getRoomTypes().size())
                .totalRooms(hotel.getRoomTypes().stream()
                        .mapToInt(rt -> rt.getRooms().size())
                        .sum())
                .build();
    }

    private LocationResponse mapLocationToResponse(Location location) {
        return LocationResponse.builder()
                .id(location.getId())
                .city(location.getCity())
                .state(location.getState())
                .country(location.getCountry())
                .address(location.getAddress())
                .zipCode(location.getZipCode())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .build();
    }

    private AmenityResponse mapAmenityToResponse(Amenity amenity) {
        return AmenityResponse.builder()
                .id(amenity.getId())
                .name(amenity.getName())
                .description(amenity.getDescription())
                .icon(amenity.getIcon())
                .build();
    }

    private PhotoResponse mapPhotoToResponse(Photo photo) {
        return PhotoResponse.builder()
                .id(photo.getId())
                .url(photo.getUrl())
                .caption(photo.getCaption())
                .displayOrder(photo.getDisplayOrder())
                .build();
    }
}
