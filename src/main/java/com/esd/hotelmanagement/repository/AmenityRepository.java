package com.esd.hotelmanagement.repository;

import com.esd.hotelmanagement.entity.Amenity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Amenity entity.
 * 
 * @author Umer
 */
@Repository
public interface AmenityRepository extends JpaRepository<Amenity, Long> {

    Optional<Amenity> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);
}
