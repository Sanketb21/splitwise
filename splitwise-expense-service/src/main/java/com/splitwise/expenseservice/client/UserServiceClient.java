package com.splitwise.expenseservice.client;

import com.splitwise.common.dto.ApiResponse;
import com.splitwise.expenseservice.dto.UserClientResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign Client for communicating with User Service
 */
@FeignClient(
    name = "user-service", 
    path = "/users",
    fallback = UserServiceClientFallback.class
)
public interface UserServiceClient {

    @GetMapping("/{id}")
    ApiResponse<UserClientResponse> getUserById(@PathVariable("id") Long id);
}

