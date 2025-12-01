package com.splitwise.groupservice.service.impl;

import com.splitwise.common.exception.BadRequestException;
import com.splitwise.common.exception.ResourceNotFoundException;
import com.splitwise.common.dto.ApiResponse;
import com.splitwise.groupservice.client.UserServiceClient;
import com.splitwise.groupservice.dto.GroupDetailResponse;
import com.splitwise.groupservice.dto.GroupMemberRequest;
import com.splitwise.groupservice.dto.GroupMemberResponse;
import com.splitwise.groupservice.dto.GroupRequest;
import com.splitwise.groupservice.dto.GroupResponse;
import com.splitwise.groupservice.dto.UserClientResponse;
import com.splitwise.groupservice.entity.Group;
import com.splitwise.groupservice.entity.GroupMember;
import com.splitwise.groupservice.repository.GroupMemberRepository;
import com.splitwise.groupservice.repository.GroupRepository;
import com.splitwise.groupservice.service.GroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import feign.FeignException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of GroupService
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class GroupServiceImpl implements GroupService {
    
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserServiceClient userServiceClient;
    
    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_MEMBER = "MEMBER";
    
    @Override
    public GroupResponse createGroup(GroupRequest groupRequest, Long createdBy) {
        log.info("Creating new group '{}' by user ID: {}", groupRequest.getName(), createdBy);

        validateUserExists(createdBy);
        
        // Check if group name already exists (case-insensitive)
        if (groupRepository.existsByNameIgnoreCaseAndIsActiveTrue(groupRequest.getName())) {
            throw new BadRequestException("Group name already exists: " + groupRequest.getName());
        }
        
        // Create new group
        Group group = Group.builder()
                .name(groupRequest.getName())
                .description(groupRequest.getDescription())
                .createdBy(createdBy)
                .isActive(true)
                .build();
        
        Group savedGroup = groupRepository.save(group);
        log.info("Group created successfully with ID: {}", savedGroup.getId());
        
        // Add creator as ADMIN member
        GroupMember creatorMember = GroupMember.builder()
                .groupId(savedGroup.getId())
                .userId(createdBy)
                .role(ROLE_ADMIN)
                .isActive(true)
                .build();
        
        groupMemberRepository.save(creatorMember);
        log.info("Creator added as ADMIN member to group ID: {}", savedGroup.getId());
        
        return mapToGroupResponse(savedGroup);
    }
    
    @Override
    @Transactional(readOnly = true)
    public GroupResponse getGroupById(Long id) {
        log.info("Fetching group by ID: {}", id);
        Group group = groupRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Group", "id", id));
        return mapToGroupResponse(group);
    }
    
    @Override
    @Transactional(readOnly = true)
    public GroupDetailResponse getGroupDetailsById(Long id) {
        log.info("Fetching group details by ID: {}", id);
        Group group = groupRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Group", "id", id));
        
        List<GroupMemberResponse> members = groupMemberRepository.findByGroupIdAndIsActiveTrue(id)
                .stream()
                .map(this::mapToGroupMemberResponse)
                .collect(Collectors.toList());
        
        return GroupDetailResponse.builder()
                .id(group.getId())
                .name(group.getName())
                .description(group.getDescription())
                .createdBy(group.getCreatedBy())
                .createdAt(group.getCreatedAt())
                .updatedAt(group.getUpdatedAt())
                .isActive(group.getIsActive())
                .members(members)
                .build();
    }
    
    @Override
    public GroupResponse updateGroup(Long id, GroupRequest groupRequest, Long userId) {
        log.info("Updating group ID: {} by user ID: {}", id, userId);
        
        validateUserExists(userId);

        Group group = groupRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Group", "id", id));
        
        // Check if user is creator or admin
        if (!isUserCreatorOrAdmin(group.getId(), userId)) {
            throw new BadRequestException("Only group creator or admin can update the group");
        }
        
        // Check if new name already exists (if name is being changed)
        if (!group.getName().equals(groupRequest.getName()) && 
            groupRepository.existsByNameIgnoreCaseAndIsActiveTrue(groupRequest.getName())) {
            throw new BadRequestException("Group name already exists: " + groupRequest.getName());
        }
        
        // Update group fields
        group.setName(groupRequest.getName());
        group.setDescription(groupRequest.getDescription());
        
        Group updatedGroup = groupRepository.save(group);
        log.info("Group updated successfully with ID: {}", updatedGroup.getId());
        
        return mapToGroupResponse(updatedGroup);
    }
    
    @Override
    public void deleteGroup(Long id, Long userId) {
        log.info("Deleting group ID: {} by user ID: {}", id, userId);
        
        validateUserExists(userId);

        Group group = groupRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Group", "id", id));
        
        // Check if user is creator or admin
        if (!isUserCreatorOrAdmin(id, userId)) {
            throw new BadRequestException("Only group creator or admin can delete the group");
        }
        
        // Soft delete - set isActive to false
        group.setIsActive(false);
        groupRepository.save(group);
        
        // Also deactivate all members
        List<GroupMember> members = groupMemberRepository.findByGroupIdAndIsActiveTrue(id);
        members.forEach(member -> member.setIsActive(false));
        groupMemberRepository.saveAll(members);
        
        log.info("Group deleted (deactivated) with ID: {}", id);
    }
    
    @Override
    public GroupMemberResponse addMemberToGroup(Long groupId, GroupMemberRequest memberRequest, Long addedBy) {
        log.info("Adding user ID: {} to group ID: {} by user ID: {}", memberRequest.getUserId(), groupId, addedBy);
        
        validateUserExists(addedBy);
        validateUserExists(memberRequest.getUserId());

        // Check if group exists and is active
        Group group = groupRepository.findByIdAndIsActiveTrue(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group", "id", groupId));
        
        // Check if user adding member is creator or admin
        if (!isUserCreatorOrAdmin(groupId, addedBy)) {
            throw new BadRequestException("Only group creator or admin can add members");
        }
        
        // Check if user is already a member
        if (groupMemberRepository.existsByGroupIdAndUserIdAndIsActiveTrue(groupId, memberRequest.getUserId())) {
            throw new BadRequestException("User is already a member of this group");
        }
        
        // Determine role (default to MEMBER if not provided or invalid)
        String role = memberRequest.getRole();
        if (role == null || role.isEmpty() || (!role.equalsIgnoreCase(ROLE_ADMIN) && !role.equalsIgnoreCase(ROLE_MEMBER))) {
            role = ROLE_MEMBER;
        } else {
            role = role.toUpperCase();
        }
        
        // Create group member
        GroupMember member = GroupMember.builder()
                .groupId(groupId)
                .userId(memberRequest.getUserId())
                .role(role)
                .isActive(true)
                .build();
        
        GroupMember savedMember = groupMemberRepository.save(member);
        log.info("Member added successfully to group ID: {}", groupId);
        
        return mapToGroupMemberResponse(savedMember);
    }
    
    @Override
    public void removeMemberFromGroup(Long groupId, Long userId, Long removedBy) {
        log.info("Removing user ID: {} from group ID: {} by user ID: {}", userId, groupId, removedBy);
        
        validateUserExists(removedBy);

        // Check if group exists and is active
        Group group = groupRepository.findByIdAndIsActiveTrue(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group", "id", groupId));
        
        // Find the member
        GroupMember member = groupMemberRepository.findByGroupIdAndUserIdAndIsActiveTrue(groupId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("GroupMember", "groupId and userId", groupId + ", " + userId));
        
        // Check if user removing is creator, admin, or the user themselves
        boolean isCreator = group.getCreatedBy().equals(removedBy);
        boolean isAdmin = isUserAdmin(groupId, removedBy);
        boolean isRemovingSelf = userId.equals(removedBy);
        
        if (!isCreator && !isAdmin && !isRemovingSelf) {
            throw new BadRequestException("Only group creator, admin, or the member themselves can remove a member");
        }
        
        // Prevent removing the creator
        if (group.getCreatedBy().equals(userId)) {
            throw new BadRequestException("Cannot remove the group creator");
        }
        
        // Soft delete - set isActive to false
        member.setIsActive(false);
        groupMemberRepository.save(member);
        
        log.info("Member removed successfully from group ID: {}", groupId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<GroupResponse> getUserGroups(Long userId) {
        log.info("Fetching all groups for user ID: {}", userId);
        validateUserExists(userId);
        List<GroupMember> memberships = groupMemberRepository.findByUserIdAndIsActiveTrue(userId);
        
        List<Long> groupIds = memberships.stream()
                .map(GroupMember::getGroupId)
                .collect(Collectors.toList());
        
        if (groupIds.isEmpty()) {
            return List.of();
        }
        
        return groupRepository.findAllById(groupIds).stream()
                .filter(group -> group.getIsActive())
                .map(this::mapToGroupResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<GroupResponse> getGroupsCreatedByUser(Long userId) {
        log.info("Fetching all groups created by user ID: {}", userId);
        validateUserExists(userId);
        return groupRepository.findByCreatedByAndIsActiveTrue(userId).stream()
                .map(this::mapToGroupResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<GroupMemberResponse> getGroupMembers(Long groupId) {
        log.info("Fetching all members for group ID: {}", groupId);
        
        // Check if group exists and is active
        groupRepository.findByIdAndIsActiveTrue(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group", "id", groupId));
        
        return groupMemberRepository.findByGroupIdAndIsActiveTrue(groupId).stream()
                .map(this::mapToGroupMemberResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<GroupResponse> searchGroups(String searchTerm, Pageable pageable) {
        log.info("Searching groups with term: {}", searchTerm);
        Page<Group> groups;
        if (searchTerm == null || searchTerm.isBlank()) {
            groups = groupRepository.findByIsActiveTrue(pageable);
        } else {
            groups = groupRepository.searchActiveGroups(searchTerm, pageable);
        }
        return groups.map(this::mapToGroupResponse);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<GroupResponse> getAllActiveGroups(Pageable pageable) {
        log.info("Fetching all active groups");
        Page<Group> groups = groupRepository.findByIsActiveTrue(pageable);
        return groups.map(this::mapToGroupResponse);
    }
    
    @Override
    public GroupMemberResponse updateMemberRole(Long groupId, Long userId, String role, Long updatedBy) {
        log.info("Updating role for user ID: {} in group ID: {} to role: {} by user ID: {}", userId, groupId, role, updatedBy);
        
        validateUserExists(updatedBy);
        validateUserExists(userId);

        // Check if group exists and is active
        Group group = groupRepository.findByIdAndIsActiveTrue(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group", "id", groupId));
        
        // Check if user updating role is creator or admin
        if (!isUserCreatorOrAdmin(groupId, updatedBy)) {
            throw new BadRequestException("Only group creator or admin can update member roles");
        }
        
        // Find the member
        GroupMember member = groupMemberRepository.findByGroupIdAndUserIdAndIsActiveTrue(groupId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("GroupMember", "groupId and userId", groupId + ", " + userId));
        
        // Prevent changing the creator's role
        if (group.getCreatedBy().equals(userId)) {
            throw new BadRequestException("Cannot change the group creator's role");
        }
        
        // Validate and normalize role
        String normalizedRole = role != null ? role.toUpperCase() : "";
        if (!normalizedRole.equals(ROLE_ADMIN) && !normalizedRole.equals(ROLE_MEMBER)) {
            throw new BadRequestException("Invalid role. Role must be either ADMIN or MEMBER");
        }
        
        // Update role
        member.setRole(normalizedRole);
        GroupMember updatedMember = groupMemberRepository.save(member);
        log.info("Member role updated successfully for user ID: {} in group ID: {}", userId, groupId);
        
        return mapToGroupMemberResponse(updatedMember);
    }
    
    /**
     * Check if user is creator or admin of the group
     */
    private boolean isUserCreatorOrAdmin(Long groupId, Long userId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group", "id", groupId));
        
        // Check if user is creator
        if (group.getCreatedBy().equals(userId)) {
            return true;
        }
        
        // Check if user is admin
        return isUserAdmin(groupId, userId);
    }
    
    /**
     * Check if user is admin of the group
     */
    private boolean isUserAdmin(Long groupId, Long userId) {
        return groupMemberRepository.findByGroupIdAndUserIdAndIsActiveTrue(groupId, userId)
                .map(member -> ROLE_ADMIN.equals(member.getRole()))
                .orElse(false);
    }

    /**
     * Validates that the given user exists by calling the User Service
     */
    private void validateUserExists(Long userId) {
        try {
            ApiResponse<UserClientResponse> response = userServiceClient.getUserById(userId);
            if (response == null || !response.isSuccess() || response.getData() == null || !Boolean.TRUE.equals(response.getData().getIsActive())) {
                throw new ResourceNotFoundException("User", "id", userId);
            }
        } catch (FeignException.NotFound ex) {
            throw new ResourceNotFoundException("User", "id", userId);
        } catch (FeignException ex) {
            log.error("Failed to validate user {}: {}", userId, ex.getMessage());
            throw new BadRequestException("Unable to validate user at the moment. Please try again later.");
        }
    }
    
    /**
     * Maps Group entity to GroupResponse DTO
     */
    private GroupResponse mapToGroupResponse(Group group) {
        return GroupResponse.builder()
                .id(group.getId())
                .name(group.getName())
                .description(group.getDescription())
                .createdBy(group.getCreatedBy())
                .createdAt(group.getCreatedAt())
                .updatedAt(group.getUpdatedAt())
                .isActive(group.getIsActive())
                .build();
    }
    
    /**
     * Maps GroupMember entity to GroupMemberResponse DTO
     */
    private GroupMemberResponse mapToGroupMemberResponse(GroupMember member) {
        return GroupMemberResponse.builder()
                .id(member.getId())
                .groupId(member.getGroupId())
                .userId(member.getUserId())
                .role(member.getRole())
                .joinedAt(member.getJoinedAt())
                .isActive(member.getIsActive())
                .build();
    }
}

