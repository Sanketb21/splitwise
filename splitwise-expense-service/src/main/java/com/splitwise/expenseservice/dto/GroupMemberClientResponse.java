package com.splitwise.expenseservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO to represent group member data fetched from Group Service
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupMemberClientResponse {
    
    private Long id;
    private Long groupId;
    private Long userId;
    private String role;
    private LocalDateTime joinedAt;
    private Boolean isActive;
}

