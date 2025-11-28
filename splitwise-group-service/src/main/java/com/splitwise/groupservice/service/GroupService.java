package com.splitwise.groupservice.service;

import com.splitwise.groupservice.dto.GroupDetailResponse;
import com.splitwise.groupservice.dto.GroupMemberRequest;
import com.splitwise.groupservice.dto.GroupMemberResponse;
import com.splitwise.groupservice.dto.GroupRequest;
import com.splitwise.groupservice.dto.GroupResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for Group operations
 */
public interface GroupService {
    
    /**
     * Create a new group
     * 
     * @param groupRequest the group creation request
     * @param createdBy the user ID who is creating the group
     * @return the created group response
     */
    GroupResponse createGroup(GroupRequest groupRequest, Long createdBy);
    
    /**
     * Get group by ID
     * 
     * @param id the group ID
     * @return the group response
     */
    GroupResponse getGroupById(Long id);
    
    /**
     * Get group details with members by ID
     * 
     * @param id the group ID
     * @return the group detail response with members
     */
    GroupDetailResponse getGroupDetailsById(Long id);
    
    /**
     * Update group information
     * 
     * @param id the group ID
     * @param groupRequest the update request
     * @param userId the user ID making the request (must be creator or admin)
     * @return the updated group response
     */
    GroupResponse updateGroup(Long id, GroupRequest groupRequest, Long userId);
    
    /**
     * Delete group (soft delete - set isActive to false)
     * 
     * @param id the group ID
     * @param userId the user ID making the request (must be creator or admin)
     */
    void deleteGroup(Long id, Long userId);
    
    /**
     * Add a member to a group
     * 
     * @param groupId the group ID
     * @param memberRequest the member request
     * @param addedBy the user ID who is adding the member (must be creator or admin)
     * @return the created group member response
     */
    GroupMemberResponse addMemberToGroup(Long groupId, GroupMemberRequest memberRequest, Long addedBy);
    
    /**
     * Remove a member from a group
     * 
     * @param groupId the group ID
     * @param userId the user ID to remove
     * @param removedBy the user ID who is removing the member (must be creator, admin, or the user themselves)
     */
    void removeMemberFromGroup(Long groupId, Long userId, Long removedBy);
    
    /**
     * Get all groups a user is a member of
     * 
     * @param userId the user ID
     * @return list of groups the user is a member of
     */
    List<GroupResponse> getUserGroups(Long userId);
    
    /**
     * Get all groups created by a user
     * 
     * @param userId the user ID
     * @return list of groups created by the user
     */
    List<GroupResponse> getGroupsCreatedByUser(Long userId);
    
    /**
     * Get all members of a group
     * 
     * @param groupId the group ID
     * @return list of group members
     */
    List<GroupMemberResponse> getGroupMembers(Long groupId);
    
    /**
     * Search groups by term
     * 
     * @param searchTerm the search term
     * @param pageable pagination information
     * @return page of matching groups
     */
    Page<GroupResponse> searchGroups(String searchTerm, Pageable pageable);
    
    /**
     * Get all active groups with pagination
     * 
     * @param pageable pagination information
     * @return page of active groups
     */
    Page<GroupResponse> getAllActiveGroups(Pageable pageable);
}

