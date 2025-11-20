package com.splitwise.common.util;

import java.util.regex.Pattern;

/**
 * Utility class for common validation operations
 */
public class ValidationUtil {
    
    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    // Phone number validation pattern (supports various formats)
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^[+]?[(]?[0-9]{1,4}[)]?[-\\s.]?[(]?[0-9]{1,4}[)]?[-\\s.]?[0-9]{1,9}$"
    );
    
    // Username validation pattern (alphanumeric and underscore, 3-20 characters)
    private static final Pattern USERNAME_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9_]{3,20}$"
    );

    /**
     * Validates email format
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Validates phone number format
     */
    public static boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return false;
        }
        return PHONE_PATTERN.matcher(phoneNumber).matches();
    }

    /**
     * Validates username format
     */
    public static boolean isValidUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return USERNAME_PATTERN.matcher(username).matches();
    }

    /**
     * Validates that a string is not null or empty
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * Validates that a number is positive
     */
    public static boolean isPositive(Number number) {
        if (number == null) {
            return false;
        }
        if (number instanceof Double || number instanceof Float) {
            return number.doubleValue() > 0;
        }
        return number.longValue() > 0;
    }
}


