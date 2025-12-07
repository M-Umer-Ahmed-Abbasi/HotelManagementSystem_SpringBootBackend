package com.esd.hotelmanagement.repository;

import com.esd.hotelmanagement.entity.Hotel;
import com.esd.hotelmanagement.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repository for Hotel entity.
 * 
 * @author Umer
 */
@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {

    List<Hotel> findByOwner(User owner);

    List<Hotel> findByOwnerId(Long ownerId);

    List<Hotel> findByEnabledTrue();

    Page<Hotel> findByEnabledTrue(Pageable pageable);

    // Search by city
    @Query("SELECT h FROM Hotel h JOIN h.location l WHERE LOWER(l.city) LIKE LOWER(CONCAT('%', :city, '%')) AND h.enabled = true")
    List<Hotel> findByCityContaining(@Param("city") String city);

    // Search by city with pagination
    @Query("SELECT h FROM Hotel h JOIN h.location l WHERE LOWER(l.city) LIKE LOWER(CONCAT('%', :city, '%')) AND h.enabled = true")
    Page<Hotel> findByCityContaining(@Param("city") String city, Pageable pageable);

    // Search by star rating
    List<Hotel> findByStarRatingGreaterThanEqual(Integer minRating);

    // Complex search with multiple filters
    @Query("SELECT DISTINCT h FROM Hotel h " +
            "JOIN h.location l " +
            "LEFT JOIN h.roomTypes rt " +
            "WHERE h.enabled = true " +
            "AND (:city IS NULL OR LOWER(l.city) LIKE LOWER(CONCAT('%', :city, '%'))) " +
            "AND (:minRating IS NULL OR h.starRating >= :minRating) " +
            "AND (:minPrice IS NULL OR rt.basePrice >= :minPrice) " +
            "AND (:maxPrice IS NULL OR rt.basePrice <= :maxPrice)")
    Page<Hotel> searchHotels(
            @Param("city") String city,
            @Param("minRating") Integer minRating,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable);
}
