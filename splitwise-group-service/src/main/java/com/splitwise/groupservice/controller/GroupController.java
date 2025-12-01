package com.splitwise.groupservice.controller;

import com.splitwise.common.dto.ApiResponse;
import com.splitwise.groupservice.dto.GroupDetailResponse;
import com.splitwise.groupservice.dto.GroupMemberRequest;
import com.splitwise.groupservice.dto.GroupMemberResponse;
import com.splitwise.groupservice.dto.GroupRequest;
import com.splitwise.groupservice.dto.GroupResponse;
import com.splitwise.groupservice.service.GroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
@Validated
@Slf4j
public class GroupController {

    private final GroupService groupService;

    @PostMapping
    public ResponseEntity<ApiResponse<GroupResponse>> createGroup(
            @Valid @RequestBody GroupRequest groupRequest,
            @RequestParam("createdBy") Long createdBy) {
        log.info("Create group request by user {}", createdBy);
        GroupResponse response = groupService.createGroup(groupRequest, createdBy);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Group created successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GroupDetailResponse>> getGroupById(@PathVariable("id") Long id) {
        GroupDetailResponse response = groupService.getGroupDetailsById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<GroupResponse>> updateGroup(
            @PathVariable("id") Long id,
            @Valid @RequestBody GroupRequest groupRequest,
            @RequestParam("userId") Long userId) {
        GroupResponse response = groupService.updateGroup(id, groupRequest, userId);
        return ResponseEntity.ok(ApiResponse.success("Group updated successfully", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteGroup(
            @PathVariable("id") Long id,
            @RequestParam("userId") Long userId) {
        groupService.deleteGroup(id, userId);
        return ResponseEntity.ok(ApiResponse.success("Group deleted successfully"));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<GroupResponse>>> getUserGroups(@PathVariable("userId") Long userId) {
        List<GroupResponse> groups = groupService.getUserGroups(userId);
        return ResponseEntity.ok(ApiResponse.success(groups));
    }

    @GetMapping("/{groupId}/members")
    public ResponseEntity<ApiResponse<List<GroupMemberResponse>>> getGroupMembers(@PathVariable("groupId") Long groupId) {
        List<GroupMemberResponse> members = groupService.getGroupMembers(groupId);
        return ResponseEntity.ok(ApiResponse.success(members));
    }

    @PostMapping("/{groupId}/members")
        public ResponseEntity<ApiResponse<GroupMemberResponse>> addMember(
            @PathVariable("groupId") Long groupId,
            @Valid @RequestBody GroupMemberRequest request,
            @RequestParam("addedBy") Long addedBy) {
        GroupMemberResponse response = groupService.addMemberToGroup(groupId, request, addedBy);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Member added successfully", response));
    }

    @DeleteMapping("/{groupId}/members/{userId}")
    public ResponseEntity<ApiResponse<Void>> removeMember(
            @PathVariable("groupId") Long groupId,
            @PathVariable("userId") Long userId,
            @RequestParam("removedBy") Long removedBy) {
        groupService.removeMemberFromGroup(groupId, userId, removedBy);
        return ResponseEntity.ok(ApiResponse.success("Member removed successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<GroupResponse>>> getGroups(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<GroupResponse> result;
        if (query != null && !query.isBlank()) {
            result = groupService.searchGroups(query, pageable);
        } else {
            result = groupService.getAllActiveGroups(pageable);
        }

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PutMapping("/{groupId}/members/{userId}/role")
    public ResponseEntity<ApiResponse<GroupMemberResponse>> updateMemberRole(
            @PathVariable("groupId") Long groupId,
            @PathVariable("userId") Long userId,
            @RequestParam("role") String role,
            @RequestParam("updatedBy") Long updatedBy) {
        log.info("Updating role for user {} in group {} to {} by user {}", userId, groupId, role, updatedBy);
        GroupMemberResponse response = groupService.updateMemberRole(groupId, userId, role, updatedBy);
        return ResponseEntity.ok(ApiResponse.success("Member role updated successfully", response));
    }
}

