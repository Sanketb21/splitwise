package com.splitwise.groupservice.repository;

import com.splitwise.groupservice.entity.Group;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Group entity
 * 
 * Provides CRUD operations and custom query methods for Group management
 */
@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    
    /**
     * Find all active groups
     * 
     * @return list of active groups
     */
    List<Group> findByIsActiveTrue();
    
    /**
     * Find all active groups with pagination
     * 
     * @param pageable pagination information
     * @return page of active groups
     */
    Page<Group> findByIsActiveTrue(Pageable pageable);
    
    /**
     * Find groups created by a specific user
     * 
     * @param createdBy the user ID who created the groups
     * @return list of groups created by the user
     */
    List<Group> findByCreatedBy(Long createdBy);
    
    /**
     * Find active groups created by a specific user
     * 
     * @param createdBy the user ID who created the groups
     * @return list of active groups created by the user
     */
    List<Group> findByCreatedByAndIsActiveTrue(Long createdBy);
    
    /**
     * Find active groups created by a specific user with pagination
     * 
     * @param createdBy the user ID who created the groups
     * @param pageable pagination information
     * @return page of active groups created by the user
     */
    Page<Group> findByCreatedByAndIsActiveTrue(Long createdBy, Pageable pageable);
    
    /**
     * Find group by ID and active status
     * 
     * @param id the group ID
     * @param isActive the active status
     * @return Optional containing the group if found
     */
    Optional<Group> findByIdAndIsActive(Long id, Boolean isActive);
    
    /**
     * Find active group by ID
     * 
     * @param id the group ID
     * @return Optional containing the active group if found
     */
    Optional<Group> findByIdAndIsActiveTrue(Long id);
    
    /**
     * Check if a group exists with the given name (case-insensitive)
     * 
     * @param name the group name to check
     * @return true if group exists, false otherwise
     */
    @Query("SELECT COUNT(g) > 0 FROM Group g WHERE LOWER(g.name) = LOWER(:name) AND g.isActive = true")
    boolean existsByNameIgnoreCaseAndIsActiveTrue(@Param("name") String name);
    
    /**
     * Search groups by name (case-insensitive)
     * 
     * @param searchTerm the term to search for
     * @param pageable pagination information
     * @return page of matching active groups
     */
    @Query("SELECT g FROM Group g WHERE " +
           "g.isActive = true AND " +
           "(LOWER(g.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(g.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Group> searchActiveGroups(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    /**
     * Count active groups
     * 
     * @return count of active groups
     */
    long countByIsActiveTrue();
    
    /**
     * Count groups created by a specific user
     * 
     * @param createdBy the user ID
     * @return count of groups created by the user
     */
    long countByCreatedBy(Long createdBy);
    
    /**
     * Count active groups created by a specific user
     * 
     * @param createdBy the user ID
     * @return count of active groups created by the user
     */
    long countByCreatedByAndIsActiveTrue(Long createdBy);
}

