package com.esd.hotelmanagement.repository;

import com.esd.hotelmanagement.entity.CancellationPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for CancellationPolicy entity.
 * 
 * @author Talha
 */
@Repository
public interface CancellationPolicyRepository extends JpaRepository<CancellationPolicy, Long> {
}
