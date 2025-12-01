package com.splitwise.groupservice.client;

import com.splitwise.common.dto.ApiResponse;
import com.splitwise.groupservice.dto.UserClientResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign Client for communicating with User Service
 * Includes circuit breaker fallback for resilience
 */
@FeignClient(
    name = "user-service", 
    path = "/api/users",
    fallback = UserServiceClientFallback.class
)
public interface UserServiceClient {

    @GetMapping("/{id}")
    ApiResponse<UserClientResponse> getUserById(@PathVariable("id") Long id);
}

