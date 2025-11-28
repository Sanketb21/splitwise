package com.splitwise.groupservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for adding a member to a group
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupMemberRequest {
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    private String role; // ADMIN or MEMBER (defaults to MEMBER if not provided)
}

