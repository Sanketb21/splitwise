package com.splitwise.expenseservice.client;

import com.splitwise.common.dto.ApiResponse;
import com.splitwise.expenseservice.dto.GroupClientResponse;
import com.splitwise.expenseservice.dto.GroupMemberClientResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Fallback implementation for GroupServiceClient
 */
@Component
@Slf4j
public class GroupServiceClientFallback implements GroupServiceClient {

    @Override
    public ApiResponse<GroupClientResponse> getGroupById(Long id) {
        log.error("GroupServiceClient fallback triggered for group ID: {}. Group Service is unavailable.", id);
        return null;
    }

    @Override
    public ApiResponse<List<GroupMemberClientResponse>> getGroupMembers(Long groupId) {
        log.error("GroupServiceClient fallback triggered for group members of group ID: {}. Group Service is unavailable.", groupId);
        return null;
    }
}

