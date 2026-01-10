package com.splitwise.notificationservice.repository;

import com.splitwise.notificationservice.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Find all notifications for a specific user
     */
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * Find all unread notifications for a specific user
     */
    List<Notification> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(Long userId);

    /**
     * Count unread notifications for a specific user
     */
    long countByUserIdAndIsReadFalse(Long userId);

    /**
     * Find all notifications for a user with pagination support
     * Note: This method can be used with Pageable parameter for pagination
     */
    List<Notification> findByUserId(Long userId, org.springframework.data.domain.Pageable pageable);

    /**
     * Find all unread notifications for a user with pagination support
     */
    List<Notification> findByUserIdAndIsReadFalse(Long userId, org.springframework.data.domain.Pageable pageable);
}
