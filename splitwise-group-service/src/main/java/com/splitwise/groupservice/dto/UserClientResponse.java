package com.splitwise.groupservice.dto;

import lombok.Data;

/**
 * Minimal representation of user information returned from User Service.
 */
@Data
public class UserClientResponse {
    private Long id;
    private String username;
    private String email;
    private Boolean isActive;
}
