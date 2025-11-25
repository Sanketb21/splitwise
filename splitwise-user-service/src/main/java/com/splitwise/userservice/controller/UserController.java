package com.splitwise.userservice.controller;

import com.splitwise.common.dto.ApiResponse;
import com.splitwise.userservice.dto.LoginRequest;
import com.splitwise.userservice.dto.LoginResponse;
import com.splitwise.userservice.dto.UserRequest;
import com.splitwise.userservice.dto.UserResponse;
import com.splitwise.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for User operations
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    
    private final UserService userService;
    
    /**
     * Register a new user
     * POST /users/register
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> registerUser(@Valid @RequestBody UserRequest userRequest) {
        log.info("Register user request received for username: {}", userRequest.getUsername());
        UserResponse userResponse = userService.registerUser(userRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully", userResponse));
    }
    
    /**
     * User login
     * POST /users/login
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Login request received for: {}", loginRequest.getUsernameOrEmail());
        LoginResponse loginResponse = userService.login(loginRequest);
        return ResponseEntity.ok(ApiResponse.success(loginResponse));
    }
    
    /**
     * Get user by ID
     * GET /users/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        log.info("Get user by ID request: {}", id);
        UserResponse userResponse = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(userResponse));
    }
    
    /**
     * Get user by username
     * GET /users/username/{username}
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserByUsername(@PathVariable String username) {
        log.info("Get user by username request: {}", username);
        UserResponse userResponse = userService.getUserByUsername(username);
        return ResponseEntity.ok(ApiResponse.success(userResponse));
    }
    
    /**
     * Get user by email
     * GET /users/email/{email}
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserByEmail(@PathVariable String email) {
        log.info("Get user by email request: {}", email);
        UserResponse userResponse = userService.getUserByEmail(email);
        return ResponseEntity.ok(ApiResponse.success(userResponse));
    }
    
    /**
     * Update user profile
     * PUT /users/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequest userRequest) {
        log.info("Update user request for ID: {}", id);
        UserResponse userResponse = userService.updateUser(id, userRequest);
        return ResponseEntity.ok(ApiResponse.success("User updated successfully", userResponse));
    }
    
    /**
     * Delete user (soft delete)
     * DELETE /users/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        log.info("Delete user request for ID: {}", id);
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully"));
    }
    
    /**
     * Search users
     * GET /users/search?query={searchTerm}&page={page}&size={size}&sort={sortBy}
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> searchUsers(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        log.info("Search users request with query: {}", query);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<UserResponse> users = userService.searchUsers(query, pageable);
        
        return ResponseEntity.ok(ApiResponse.success(users));
    }
    
    /**
     * Get all active users with pagination
     * GET /users/active?page={page}&size={size}&sort={sortBy}
     */
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllActiveUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        log.info("Get all active users request");
        
        Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<UserResponse> users = userService.getAllActiveUsers(pageable);
        
        return ResponseEntity.ok(ApiResponse.success(users));
    }
    
    /**
     * Get all users with pagination
     * GET /users?page={page}&size={size}&sort={sortBy}
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        log.info("Get all users request");
        
        Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<UserResponse> users = userService.getAllUsers(pageable);
        
        return ResponseEntity.ok(ApiResponse.success(users));
    }
}

