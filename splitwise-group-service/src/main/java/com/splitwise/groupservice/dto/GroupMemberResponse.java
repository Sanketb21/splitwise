package com.splitwise.groupservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for group member response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupMemberResponse {
    
    private Long id;
    private Long groupId;
    private Long userId;
    private String role;
    private LocalDateTime joinedAt;
    private Boolean isActive;
}

