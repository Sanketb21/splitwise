package com.splitwise.userservice.service.impl;

import com.splitwise.common.exception.BadRequestException;
import com.splitwise.common.exception.ResourceNotFoundException;
import com.splitwise.userservice.dto.LoginRequest;
import com.splitwise.userservice.dto.LoginResponse;
import com.splitwise.userservice.dto.UserRequest;
import com.splitwise.userservice.dto.UserResponse;
import com.splitwise.userservice.entity.User;
import com.splitwise.userservice.repository.UserRepository;
import com.splitwise.userservice.service.UserService;
import com.splitwise.userservice.util.JwtTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of UserService
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    
    @Override
    public UserResponse registerUser(UserRequest userRequest) {
        log.info("Registering new user with username: {}", userRequest.getUsername());
        
        // Check if username already exists
        if (userRepository.existsByUsername(userRequest.getUsername())) {
            throw new BadRequestException("Username already exists: " + userRequest.getUsername());
        }
        
        // Check if email already exists
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new BadRequestException("Email already exists: " + userRequest.getEmail());
        }
        
        // Create new user
        User user = User.builder()
                .username(userRequest.getUsername())
                .email(userRequest.getEmail())
                .password(passwordEncoder.encode(userRequest.getPassword())) // Encrypt password
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .phoneNumber(userRequest.getPhoneNumber())
                .isActive(true)
                .build();
        
        User savedUser = userRepository.save(user);
        log.info("User registered successfully with ID: {}", savedUser.getId());
        
        return mapToUserResponse(savedUser);
    }
    
    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        log.info("Login attempt for: {}", loginRequest.getUsernameOrEmail());
        
        // Find user by username or email
        User user = userRepository.findByUsername(loginRequest.getUsernameOrEmail())
                .or(() -> userRepository.findByEmail(loginRequest.getUsernameOrEmail()))
                .orElseThrow(() -> new BadRequestException("Invalid username/email or password"));
        
        // Check if user is active
        if (!user.getIsActive()) {
            throw new BadRequestException("User account is inactive");
        }
        
        // Verify password
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid username/email or password");
        }
        
        // Generate JWT token
        String token = jwtTokenService.generateToken(user);
        
        log.info("User logged in successfully: {}", user.getUsername());
        
        return LoginResponse.builder()
                .token(token)
                .user(mapToUserResponse(user))
                .message("Login successful")
                .build();
    }
    
    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        log.info("Fetching user by ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return mapToUserResponse(user);
    }
    
    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByUsername(String username) {
        log.info("Fetching user by username: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        return mapToUserResponse(user);
    }
    
    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        log.info("Fetching user by email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        return mapToUserResponse(user);
    }
    
    @Override
    public UserResponse updateUser(Long id, UserRequest userRequest) {
        log.info("Updating user with ID: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        
        // Check if username is being changed and if new username already exists
        if (!user.getUsername().equals(userRequest.getUsername()) && 
            userRepository.existsByUsername(userRequest.getUsername())) {
            throw new BadRequestException("Username already exists: " + userRequest.getUsername());
        }
        
        // Check if email is being changed and if new email already exists
        if (!user.getEmail().equals(userRequest.getEmail()) && 
            userRepository.existsByEmail(userRequest.getEmail())) {
            throw new BadRequestException("Email already exists: " + userRequest.getEmail());
        }
        
        // Update user fields
        user.setUsername(userRequest.getUsername());
        user.setEmail(userRequest.getEmail());
        
        // Only update password if provided
        if (userRequest.getPassword() != null && !userRequest.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        }
        
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setPhoneNumber(userRequest.getPhoneNumber());
        
        User updatedUser = userRepository.save(user);
        log.info("User updated successfully with ID: {}", updatedUser.getId());
        
        return mapToUserResponse(updatedUser);
    }
    
    @Override
    public void deleteUser(Long id) {
        log.info("Deleting user with ID: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        
        // Soft delete - set isActive to false
        user.setIsActive(false);
        userRepository.save(user);
        
        log.info("User deleted (deactivated) with ID: {}", id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> searchUsers(String searchTerm, Pageable pageable) {
        log.info("Searching users with term: {}", searchTerm);
        Page<User> users = userRepository.searchUsers(searchTerm, pageable);
        return users.map(this::mapToUserResponse);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllActiveUsers(Pageable pageable) {
        log.info("Fetching all active users");
        Page<User> users = userRepository.findByIsActiveTrue(pageable);
        return users.map(this::mapToUserResponse);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        log.info("Fetching all users");
        Page<User> users = userRepository.findAll(pageable);
        return users.map(this::mapToUserResponse);
    }
    
    /**
     * Maps User entity to UserResponse DTO
     */
    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .isActive(user.getIsActive())
                .build();
    }
}

