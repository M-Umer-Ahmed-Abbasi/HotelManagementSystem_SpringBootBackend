package com.esd.hotelmanagement.repository;

import com.esd.hotelmanagement.entity.TransactionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for TransactionLog entity.
 * 
 * @author Talha
 */
@Repository
public interface TransactionLogRepository extends JpaRepository<TransactionLog, Long> {

    List<TransactionLog> findByPaymentIdOrderByCreatedAtDesc(Long paymentId);
}
