package com.splitwise.groupservice.client;

import com.splitwise.common.dto.ApiResponse;
import com.splitwise.groupservice.dto.UserClientResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", path = "/users")
public interface UserServiceClient {

    @GetMapping("/{id}")
    ApiResponse<UserClientResponse> getUserById(@PathVariable("id") Long id);
}

