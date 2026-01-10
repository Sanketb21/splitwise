package com.splitwise.notificationservice.enums;

/**
 * Enum representing the type of notification
 */
public enum NotificationType {
    /**
     * Notification when an expense is added
     */
    EXPENSE_ADDED,

    /**
     * Notification when a settlement is made
     */
    SETTLEMENT,

    /**
     * Notification when a user is invited to a group
     */
    GROUP_INVITATION,

    /**
     * Notification when a user is added to a group
     */
    GROUP_MEMBER_ADDED,

    /**
     * Notification when a user is removed from a group
     */
    GROUP_MEMBER_REMOVED,

    /**
     * Notification when a group is updated
     */
    GROUP_UPDATED,

    /**
     * Notification when an expense is updated
     */
    EXPENSE_UPDATED,

    /**
     * Notification when an expense is deleted
     */
    EXPENSE_DELETED,

    /**
     * Notification for balance reminders
     */
    BALANCE_REMINDER,

    /**
     * Generic notification type
     */
    GENERAL
}
