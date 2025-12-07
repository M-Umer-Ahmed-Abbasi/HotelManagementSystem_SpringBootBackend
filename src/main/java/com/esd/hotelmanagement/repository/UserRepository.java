package com.esd.hotelmanagement.repository;

import com.esd.hotelmanagement.entity.Role;
import com.esd.hotelmanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for User entity.
 * 
 * @author Umer
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByRole(Role role);

    List<User> findByEnabledTrue();

    List<User> findByEnabledFalse();
}
