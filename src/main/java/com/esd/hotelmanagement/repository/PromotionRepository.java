package com.esd.hotelmanagement.repository;

import com.esd.hotelmanagement.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Promotion entity.
 * 
 * @author Talha
 */
@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    Optional<Promotion> findByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCase(String code);

    List<Promotion> findByEnabledTrue();

    // Find valid promotions (active date range + not exceeded usage limit)
    @Query("SELECT p FROM Promotion p WHERE p.enabled = true " +
            "AND p.startDate <= :today AND p.endDate >= :today " +
            "AND (p.usageLimit IS NULL OR p.usageCount < p.usageLimit)")
    List<Promotion> findActivePromotions(@Param("today") LocalDate today);
}
