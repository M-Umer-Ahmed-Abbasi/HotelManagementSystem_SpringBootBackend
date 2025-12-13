package com.esd.hotelmanagement.service;

import com.esd.hotelmanagement.dto.StaffRequest;
import com.esd.hotelmanagement.dto.StaffResponse;
import com.esd.hotelmanagement.entity.*;
import com.esd.hotelmanagement.exception.BadRequestException;
import com.esd.hotelmanagement.exception.DuplicateResourceException;
import com.esd.hotelmanagement.exception.ResourceNotFoundException;
import com.esd.hotelmanagement.exception.UnauthorizedException;
import com.esd.hotelmanagement.repository.HotelRepository;
import com.esd.hotelmanagement.repository.StaffRepository;
import com.esd.hotelmanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for staff management.
 * 
 * @author Talha
 */
@Service
@RequiredArgsConstructor
public class StaffService {

    private final StaffRepository staffRepository;
    private final HotelRepository hotelRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    @Transactional
    public StaffResponse assignStaff(Long hotelId, StaffRequest request) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel", "id", hotelId));

        checkOwnerPermission(hotel);

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId()));

        // Verify user has STAFF role
        if (user.getRole() != Role.STAFF) {
            throw new BadRequestException("User must have STAFF role");
        }

        // Check if already assigned
        if (staffRepository.existsByUserIdAndHotelId(user.getId(), hotelId)) {
            throw new DuplicateResourceException("Staff already assigned to this hotel");
        }

        Staff staff = Staff.builder()
                .user(user)
                .hotel(hotel)
                .position(request.getPosition())
                .build();

        staff = staffRepository.save(staff);
        return mapToResponse(staff);
    }

    public List<StaffResponse> getStaffByHotel(Long hotelId) {
        return staffRepository.findByHotelId(hotelId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public StaffResponse getStaffById(Long id) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Staff", "id", id));
        return mapToResponse(staff);
    }

    @Transactional
    public StaffResponse updateStaff(Long id, StaffRequest request) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Staff", "id", id));

        checkOwnerPermission(staff.getHotel());

        staff.setPosition(request.getPosition());
        staff = staffRepository.save(staff);
        return mapToResponse(staff);
    }

    @Transactional
    public void removeStaff(Long id) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Staff", "id", id));
        checkOwnerPermission(staff.getHotel());
        staffRepository.delete(staff);
    }

    private void checkOwnerPermission(Hotel hotel) {
        User currentUser = userService.getCurrentUser();
        if (!hotel.getOwner().getId().equals(currentUser.getId()) && currentUser.getRole() != Role.ADMIN) {
            throw new UnauthorizedException("Only hotel owner can manage staff");
        }
    }

    private StaffResponse mapToResponse(Staff staff) {
        return StaffResponse.builder()
                .id(staff.getId())
                .position(staff.getPosition())
                .hireDate(staff.getHireDate())
                .userId(staff.getUser().getId())
                .userName(staff.getUser().getFirstName() + " " + staff.getUser().getLastName())
                .userEmail(staff.getUser().getEmail())
                .hotelId(staff.getHotel().getId())
                .hotelName(staff.getHotel().getName())
                .build();
    }
}
