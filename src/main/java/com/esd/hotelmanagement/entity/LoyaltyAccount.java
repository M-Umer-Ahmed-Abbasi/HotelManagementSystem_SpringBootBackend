package com.esd.hotelmanagement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * LoyaltyAccount entity for guest loyalty points.
 * 
 * @author Talha
 */
@Entity
@Table(name = "loyalty_accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoyaltyAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Builder.Default
    private Integer points = 0;

    private String tier; // e.g., BRONZE, SILVER, GOLD, PLATINUM

    @Column(updatable = false)
    private LocalDateTime createdAt;

    // ==================== Relationship ====================

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // ==================== Lifecycle ====================

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (tier == null) {
            tier = "BRONZE";
        }
    }

    // ==================== Helper Methods ====================

    public void addPoints(int amount) {
        this.points += amount;
        updateTier();
    }

    private void updateTier() {
        if (points >= 10000) {
            tier = "PLATINUM";
        } else if (points >= 5000) {
            tier = "GOLD";
        } else if (points >= 1000) {
            tier = "SILVER";
        } else {
            tier = "BRONZE";
        }
    }
}
