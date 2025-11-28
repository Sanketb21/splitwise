package com.splitwise.groupservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for group detail response with members
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupDetailResponse {
    
    private Long id;
    private String name;
    private String description;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isActive;
    private List<GroupMemberResponse> members;
}

