package com.esd.hotelmanagement.service;

import com.esd.hotelmanagement.dto.AmenityRequest;
import com.esd.hotelmanagement.dto.AmenityResponse;
import com.esd.hotelmanagement.entity.Amenity;
import com.esd.hotelmanagement.exception.DuplicateResourceException;
import com.esd.hotelmanagement.exception.ResourceNotFoundException;
import com.esd.hotelmanagement.repository.AmenityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for amenity operations.
 * 
 * @author Umer
 */
@Service
@RequiredArgsConstructor
public class AmenityService {

    private final AmenityRepository amenityRepository;

    public List<AmenityResponse> getAllAmenities() {
        return amenityRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public AmenityResponse getAmenityById(Long id) {
        Amenity amenity = amenityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Amenity", "id", id));
        return mapToResponse(amenity);
    }

    @Transactional
    public AmenityResponse createAmenity(AmenityRequest request) {
        if (amenityRepository.existsByNameIgnoreCase(request.getName())) {
            throw new DuplicateResourceException("Amenity", "name", request.getName());
        }

        Amenity amenity = Amenity.builder()
                .name(request.getName())
                .description(request.getDescription())
                .icon(request.getIcon())
                .build();

        amenity = amenityRepository.save(amenity);
        return mapToResponse(amenity);
    }

    @Transactional
    public AmenityResponse updateAmenity(Long id, AmenityRequest request) {
        Amenity amenity = amenityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Amenity", "id", id));

        amenity.setName(request.getName());
        amenity.setDescription(request.getDescription());
        amenity.setIcon(request.getIcon());

        amenity = amenityRepository.save(amenity);
        return mapToResponse(amenity);
    }

    @Transactional
    public void deleteAmenity(Long id) {
        Amenity amenity = amenityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Amenity", "id", id));
        amenityRepository.delete(amenity);
    }

    private AmenityResponse mapToResponse(Amenity amenity) {
        return AmenityResponse.builder()
                .id(amenity.getId())
                .name(amenity.getName())
                .description(amenity.getDescription())
                .icon(amenity.getIcon())
                .build();
    }
}
