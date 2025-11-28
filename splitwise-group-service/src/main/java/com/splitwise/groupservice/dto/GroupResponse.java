package com.splitwise.groupservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for group response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupResponse {
    
    private Long id;
    private String name;
    private String description;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isActive;
}

