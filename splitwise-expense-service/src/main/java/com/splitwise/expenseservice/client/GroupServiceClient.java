package com.splitwise.expenseservice.client;

import com.splitwise.common.dto.ApiResponse;
import com.splitwise.expenseservice.dto.GroupClientResponse;
import com.splitwise.expenseservice.dto.GroupMemberClientResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * Feign Client for communicating with Group Service
 */
@FeignClient(
    name = "group-service", 
    path = "/api/groups",
    fallback = GroupServiceClientFallback.class
)
public interface GroupServiceClient {

    @GetMapping("/{id}")
    ApiResponse<GroupClientResponse> getGroupById(@PathVariable("id") Long id);

    @GetMapping("/{groupId}/members")
    ApiResponse<List<GroupMemberClientResponse>> getGroupMembers(@PathVariable("groupId") Long groupId);
}

