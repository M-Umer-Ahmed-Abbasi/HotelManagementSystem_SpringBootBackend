package com.esd.hotelmanagement.repository;

import com.esd.hotelmanagement.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Staff entity.
 * 
 * @author Talha
 */
@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {

    List<Staff> findByHotelId(Long hotelId);

    List<Staff> findByUserId(Long userId);

    Optional<Staff> findByUserIdAndHotelId(Long userId, Long hotelId);

    boolean existsByUserIdAndHotelId(Long userId, Long hotelId);
}
