package com.esd.hotelmanagement.repository;

import com.esd.hotelmanagement.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Location entity.
 * 
 * @author Umer
 */
@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    List<Location> findByCityContainingIgnoreCase(String city);

    List<Location> findByCountryContainingIgnoreCase(String country);

    List<Location> findByCityAndCountry(String city, String country);
}
