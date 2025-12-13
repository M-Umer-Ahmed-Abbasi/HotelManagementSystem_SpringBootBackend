package com.esd.hotelmanagement.repository;

import com.esd.hotelmanagement.entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Photo entity.
 * 
 * @author Talha
 */
@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {

    List<Photo> findByHotelIdOrderByDisplayOrderAsc(Long hotelId);
}
