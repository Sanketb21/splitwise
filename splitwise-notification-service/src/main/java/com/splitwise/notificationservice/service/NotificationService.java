package com.splitwise.notificationservice.service;

import com.splitwise.notificationservice.dto.NotificationRequest;
import com.splitwise.notificationservice.dto.NotificationResponse;

import java.util.List;

public interface NotificationService {

    /**
     * Create a new notification
     */
    NotificationResponse createNotification(NotificationRequest request);

    /**
     * Get a notification by ID
     */
    NotificationResponse getNotification(Long id);

    /**
     * Get all notifications for a user
     */
    List<NotificationResponse> getUserNotifications(Long userId);

    /**
     * Get unread notifications for a user
     */
    List<NotificationResponse> getUnreadNotifications(Long userId);

    /**
     * Mark a notification as read
     */
    NotificationResponse markAsRead(Long id);

    /**
     * Mark all notifications as read for a user
     */
    void markAllAsRead(Long userId);

    /**
     * Get count of unread notifications for a user
     */
    long getUnreadCount(Long userId);

    /**
     * Delete a notification by ID
     */
    void deleteNotification(Long id);
}
