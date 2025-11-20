package com.splitwise.common.constants;

/**
 * Application-wide constants
 */
public class AppConstants {
    
    // Date and Time Formats
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    
    // Pagination
    public static final String DEFAULT_PAGE_NUMBER = "0";
    public static final String DEFAULT_PAGE_SIZE = "10";
    public static final String DEFAULT_SORT_BY = "id";
    public static final String DEFAULT_SORT_DIRECTION = "asc";
    
    // User Roles
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_MEMBER = "MEMBER";
    
    // Group Member Roles
    public static final String GROUP_ROLE_ADMIN = "ADMIN";
    public static final String GROUP_ROLE_MEMBER = "MEMBER";
    
    // Expense Split Types
    public static final String SPLIT_TYPE_EQUAL = "EQUAL";
    public static final String SPLIT_TYPE_UNEQUAL = "UNEQUAL";
    public static final String SPLIT_TYPE_PERCENTAGE = "PERCENTAGE";
    public static final String SPLIT_TYPE_SHARES = "SHARES";
    
    // Settlement Status
    public static final String SETTLEMENT_STATUS_PENDING = "PENDING";
    public static final String SETTLEMENT_STATUS_COMPLETED = "COMPLETED";
    public static final String SETTLEMENT_STATUS_CANCELLED = "CANCELLED";
    
    // Notification Types
    public static final String NOTIFICATION_TYPE_EXPENSE_ADDED = "EXPENSE_ADDED";
    public static final String NOTIFICATION_TYPE_SETTLEMENT = "SETTLEMENT";
    public static final String NOTIFICATION_TYPE_GROUP_INVITATION = "GROUP_INVITATION";
    public static final String NOTIFICATION_TYPE_MEMBER_ADDED = "MEMBER_ADDED";
    public static final String NOTIFICATION_TYPE_MEMBER_REMOVED = "MEMBER_REMOVED";
    
    // API Messages
    public static final String SUCCESS_MESSAGE = "Operation completed successfully";
    public static final String ERROR_MESSAGE = "An error occurred";
    public static final String NOT_FOUND_MESSAGE = "Resource not found";
    public static final String VALIDATION_ERROR_MESSAGE = "Validation failed";
    
    private AppConstants() {
        // Private constructor to prevent instantiation
    }
}


