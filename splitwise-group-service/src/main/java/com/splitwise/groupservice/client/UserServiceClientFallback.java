package com.splitwise.groupservice.client;

import com.splitwise.common.dto.ApiResponse;
import com.splitwise.groupservice.dto.UserClientResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Fallback implementation for UserServiceClient
 * Used when the User Service is unavailable or returns an error
 */
@Component
@Slf4j
public class UserServiceClientFallback implements UserServiceClient {

    @Override
    public ApiResponse<UserClientResponse> getUserById(Long id) {
        log.error("UserServiceClient fallback triggered for user ID: {}. User Service is unavailable.", id);
        // Return a response indicating the service is unavailable
        // This will be handled by the service layer
        return null;
    }
}

