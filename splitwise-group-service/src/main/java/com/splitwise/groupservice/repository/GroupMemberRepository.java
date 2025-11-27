package com.splitwise.groupservice.repository;

import com.splitwise.groupservice.entity.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for GroupMember entity
 * 
 * Provides CRUD operations and custom query methods for GroupMember management
 */
@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    
    /**
     * Find all members of a group
     * 
     * @param groupId the group ID
     * @return list of group members
     */
    List<GroupMember> findByGroupId(Long groupId);
    
    /**
     * Find all active members of a group
     * 
     * @param groupId the group ID
     * @return list of active group members
     */
    List<GroupMember> findByGroupIdAndIsActiveTrue(Long groupId);
    
    /**
     * Find all groups a user is a member of
     * 
     * @param userId the user ID
     * @return list of group memberships
     */
    List<GroupMember> findByUserId(Long userId);
    
    /**
     * Find all active groups a user is a member of
     * 
     * @param userId the user ID
     * @return list of active group memberships
     */
    List<GroupMember> findByUserIdAndIsActiveTrue(Long userId);
    
    /**
     * Find a specific group membership
     * 
     * @param groupId the group ID
     * @param userId the user ID
     * @return Optional containing the group member if found
     */
    Optional<GroupMember> findByGroupIdAndUserId(Long groupId, Long userId);
    
    /**
     * Find an active group membership
     * 
     * @param groupId the group ID
     * @param userId the user ID
     * @return Optional containing the active group member if found
     */
    Optional<GroupMember> findByGroupIdAndUserIdAndIsActiveTrue(Long groupId, Long userId);
    
    /**
     * Check if a user is a member of a group
     * 
     * @param groupId the group ID
     * @param userId the user ID
     * @return true if user is a member, false otherwise
     */
    boolean existsByGroupIdAndUserId(Long groupId, Long userId);
    
    /**
     * Check if a user is an active member of a group
     * 
     * @param groupId the group ID
     * @param userId the user ID
     * @return true if user is an active member, false otherwise
     */
    boolean existsByGroupIdAndUserIdAndIsActiveTrue(Long groupId, Long userId);
    
    /**
     * Find all members of a group with a specific role
     * 
     * @param groupId the group ID
     * @param role the role (ADMIN, MEMBER)
     * @return list of group members with the specified role
     */
    List<GroupMember> findByGroupIdAndRole(Long groupId, String role);
    
    /**
     * Find all active members of a group with a specific role
     * 
     * @param groupId the group ID
     * @param role the role (ADMIN, MEMBER)
     * @return list of active group members with the specified role
     */
    List<GroupMember> findByGroupIdAndRoleAndIsActiveTrue(Long groupId, String role);
    
    /**
     * Find all groups where a user has a specific role
     * 
     * @param userId the user ID
     * @param role the role (ADMIN, MEMBER)
     * @return list of group memberships with the specified role
     */
    List<GroupMember> findByUserIdAndRole(Long userId, String role);
    
    /**
     * Find all active groups where a user has a specific role
     * 
     * @param userId the user ID
     * @param role the role (ADMIN, MEMBER)
     * @return list of active group memberships with the specified role
     */
    List<GroupMember> findByUserIdAndRoleAndIsActiveTrue(Long userId, String role);
    
    /**
     * Count members in a group
     * 
     * @param groupId the group ID
     * @return count of members in the group
     */
    long countByGroupId(Long groupId);
    
    /**
     * Count active members in a group
     * 
     * @param groupId the group ID
     * @return count of active members in the group
     */
    long countByGroupIdAndIsActiveTrue(Long groupId);
    
    /**
     * Count groups a user is a member of
     * 
     * @param userId the user ID
     * @return count of groups the user is a member of
     */
    long countByUserId(Long userId);
    
    /**
     * Count active groups a user is a member of
     * 
     * @param userId the user ID
     * @return count of active groups the user is a member of
     */
    long countByUserIdAndIsActiveTrue(Long userId);
    
    /**
     * Count admins in a group
     * 
     * @param groupId the group ID
     * @return count of admins in the group
     */
    long countByGroupIdAndRoleAndIsActiveTrue(Long groupId, String role);
    
    /**
     * Find all members of multiple groups
     * 
     * @param groupIds list of group IDs
     * @return list of group members
     */
    @Query("SELECT gm FROM GroupMember gm WHERE gm.groupId IN :groupIds AND gm.isActive = true")
    List<GroupMember> findByGroupIdInAndIsActiveTrue(@Param("groupIds") List<Long> groupIds);
}

