package com.splitwise.groupservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO to represent user data fetched from User Service
 * Must match UserResponse structure from User Service
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserClientResponse {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isActive;
}
