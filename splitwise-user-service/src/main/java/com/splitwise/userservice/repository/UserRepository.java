package com.splitwise.userservice.repository;

import com.splitwise.userservice.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity
 * 
 * Provides CRUD operations and custom query methods for User management
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Find user by username
     * 
     * @param username the username to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Find user by email
     * 
     * @param email the email to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Check if a user exists with the given username
     * 
     * @param username the username to check
     * @return true if user exists, false otherwise
     */
    boolean existsByUsername(String username);
    
    /**
     * Check if a user exists with the given email
     * 
     * @param email the email to check
     * @return true if user exists, false otherwise
     */
    boolean existsByEmail(String email);
    
    /**
     * Find all active users
     * 
     * @return list of active users
     */
    List<User> findByIsActiveTrue();
    
    /**
     * Find all active users with pagination
     * 
     * @param pageable pagination information
     * @return page of active users
     */
    Page<User> findByIsActiveTrue(Pageable pageable);
    
    /**
     * Find all inactive users
     * 
     * @return list of inactive users
     */
    List<User> findByIsActiveFalse();
    
    /**
     * Search users by username or email (case-insensitive)
     * 
     * @param searchTerm the term to search for
     * @param pageable pagination information
     * @return page of matching users
     */
    @Query("SELECT u FROM User u WHERE " +
           "LOWER(u.username) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<User> searchUsers(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    /**
     * Find users by first name and last name
     * 
     * @param firstName the first name
     * @param lastName the last name
     * @return list of matching users
     */
    List<User> findByFirstNameAndLastName(String firstName, String lastName);
    
    /**
     * Find active users by first name and last name
     * 
     * @param firstName the first name
     * @param lastName the last name
     * @return list of matching active users
     */
    List<User> findByFirstNameAndLastNameAndIsActiveTrue(String firstName, String lastName);
    
    /**
     * Count active users
     * 
     * @return count of active users
     */
    long countByIsActiveTrue();
    
    /**
     * Count inactive users
     * 
     * @return count of inactive users
     */
    long countByIsActiveFalse();
}

