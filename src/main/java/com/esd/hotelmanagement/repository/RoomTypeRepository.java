package com.esd.hotelmanagement.repository;

import com.esd.hotelmanagement.entity.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repository for RoomType entity.
 * 
 * @author Umer
 */
@Repository
public interface RoomTypeRepository extends JpaRepository<RoomType, Long> {

    List<RoomType> findByHotelId(Long hotelId);

    List<RoomType> findByBasePriceLessThanEqual(BigDecimal maxPrice);

    List<RoomType> findByBasePriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    List<RoomType> findByMaxOccupancyGreaterThanEqual(Integer minOccupancy);
}
