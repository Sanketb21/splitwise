package com.splitwise.expenseservice.client;

import com.splitwise.common.dto.ApiResponse;
import com.splitwise.expenseservice.dto.UserClientResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Fallback implementation for UserServiceClient
 */
@Component
@Slf4j
public class UserServiceClientFallback implements UserServiceClient {

    @Override
    public ApiResponse<UserClientResponse> getUserById(Long id) {
        log.error("UserServiceClient fallback triggered for user ID: {}. User Service is unavailable.", id);
        return null;
    }
}

