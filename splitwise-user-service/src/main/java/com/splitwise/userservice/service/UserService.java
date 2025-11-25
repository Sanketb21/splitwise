package com.splitwise.userservice.service;

import com.splitwise.userservice.dto.LoginRequest;
import com.splitwise.userservice.dto.LoginResponse;
import com.splitwise.userservice.dto.UserRequest;
import com.splitwise.userservice.dto.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for User operations
 */
public interface UserService {
    
    /**
     * Register a new user
     * 
     * @param userRequest the user registration request
     * @return the created user response
     */
    UserResponse registerUser(UserRequest userRequest);
    
    /**
     * Authenticate user and generate JWT token
     * 
     * @param loginRequest the login request
     * @return login response with token and user details
     */
    LoginResponse login(LoginRequest loginRequest);
    
    /**
     * Get user by ID
     * 
     * @param id the user ID
     * @return the user response
     */
    UserResponse getUserById(Long id);
    
    /**
     * Get user by username
     * 
     * @param username the username
     * @return the user response
     */
    UserResponse getUserByUsername(String username);
    
    /**
     * Get user by email
     * 
     * @param email the email
     * @return the user response
     */
    UserResponse getUserByEmail(String email);
    
    /**
     * Update user profile
     * 
     * @param id the user ID
     * @param userRequest the update request
     * @return the updated user response
     */
    UserResponse updateUser(Long id, UserRequest userRequest);
    
    /**
     * Delete user (soft delete - set isActive to false)
     * 
     * @param id the user ID
     */
    void deleteUser(Long id);
    
    /**
     * Search users by term
     * 
     * @param searchTerm the search term
     * @param pageable pagination information
     * @return page of matching users
     */
    Page<UserResponse> searchUsers(String searchTerm, Pageable pageable);
    
    /**
     * Get all active users with pagination
     * 
     * @param pageable pagination information
     * @return page of active users
     */
    Page<UserResponse> getAllActiveUsers(Pageable pageable);
    
    /**
     * Get all users with pagination
     * 
     * @param pageable pagination information
     * @return page of all users
     */
    Page<UserResponse> getAllUsers(Pageable pageable);
}

