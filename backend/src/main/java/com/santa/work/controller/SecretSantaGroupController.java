package com.santa.work.controller;

import com.santa.work.dto.SecretSantaGroupDTO;
import com.santa.work.dto.UserDTO;
import com.santa.work.entity.SecretSantaGroup;
import com.santa.work.entity.Users;
import com.santa.work.mapper.SecretSantaGroupMapper;
import com.santa.work.mapper.UserMapper;
import com.santa.work.service.secretSantaGroup.SecretSantaGroupServiceImpl;
import com.santa.work.service.user.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/groups")
@Slf4j
public class SecretSantaGroupController {
    private final SecretSantaGroupServiceImpl secretSantaGroupService;
    private final UserServiceImpl userService;
    private final SecretSantaGroupMapper secretSantaGroupMapper;
    private final UserMapper userMapper;

    @Autowired
    public SecretSantaGroupController(@Lazy SecretSantaGroupServiceImpl secretSantaGroupService, UserServiceImpl userService, @Lazy SecretSantaGroupMapper secretSantaGroupMapper,@Lazy UserMapper userMapper) {
        this.secretSantaGroupService = secretSantaGroupService;
        this.userService = userService;
        this.secretSantaGroupMapper = secretSantaGroupMapper;
        this.userMapper = userMapper;
    }

    //Create
    @Operation(summary = "Create a new Secret Santa Group")
    @PostMapping
    public ResponseEntity<SecretSantaGroupDTO> createSecretSantaGroup(@RequestBody SecretSantaGroupDTO secretSantaGroupDTO) {
        UUID adminId = secretSantaGroupDTO.getAdminId();
        Users currentUser = userService.findUserById(adminId);
        SecretSantaGroup createdGroup = secretSantaGroupService.createSecretSantaGroup(secretSantaGroupMapper.toSecretSantaGroupEntity(secretSantaGroupDTO, adminId), currentUser.getId());
        return new ResponseEntity<>(secretSantaGroupMapper.toSecretSantaGroupDTO(createdGroup), HttpStatus.CREATED);
    }

    @Operation(summary = "Add an user to the group")
    @PostMapping("/{groupId}/addUser/{userId}")
    public ResponseEntity<SecretSantaGroupDTO> addUserToSecretSantaGroup(@PathVariable UUID userId, @PathVariable UUID groupId) {
        SecretSantaGroup group = secretSantaGroupService.addUserToGroup(userId, groupId);
        return new ResponseEntity<>(secretSantaGroupMapper.toSecretSantaGroupDTO(group), HttpStatus.OK);
    }
    @Operation(summary = "Find a precise secret santa group by id")
    @GetMapping("/{groupId}")
    public ResponseEntity<SecretSantaGroupDTO> getSecretSantaGroupById(@PathVariable UUID groupId) {
        SecretSantaGroup group = secretSantaGroupService.findSecretSantaGroupById(groupId);
        return new ResponseEntity<>(secretSantaGroupMapper.toSecretSantaGroupDTO(group), HttpStatus.OK);
    }

    @Operation(summary = "Get all member of a group")
    @GetMapping("/{groupId}/members")
    public ResponseEntity<List<UserDTO>> getAllMembersOfSecretSantaGroup(@PathVariable UUID groupId) {
        List<Users> members = secretSantaGroupService.findAllMembersSantaGroups(groupId);
        List<UserDTO> membersDTO = members.stream().map(userMapper::toUserDTO).collect(Collectors.toList());
        return new ResponseEntity<>(membersDTO, HttpStatus.OK);
    }

    @Operation(summary = "Get all groups")
    @GetMapping("/all")
    public ResponseEntity<List<SecretSantaGroupDTO>> getAllSecretSantaGroups() {
        //List<SecretSantaGroup> group = secretSantaGroupService.getAllSecretSantaGroups();
        List<SecretSantaGroup> groups = secretSantaGroupService.findAllSecretSantaGroups();
        return new ResponseEntity<>(secretSantaGroupMapper.toSecretSantaGroupDTOs(groups), HttpStatus.OK);
    }

    //Update

    @PutMapping("/{groupId}")
    public ResponseEntity<SecretSantaGroupDTO> updateSecretSantaGroup(@PathVariable UUID groupId, @Valid @RequestBody SecretSantaGroupDTO updatedGroupDTO) {
        SecretSantaGroup group = secretSantaGroupService.updateSecretSantaGroup(groupId, updatedGroupDTO);
        return new ResponseEntity<>(secretSantaGroupMapper.toSecretSantaGroupDTO(group), HttpStatus.OK);
    }

    //Delete

    @Operation(summary = "Delete a group")
    @DeleteMapping("/{groupId}")
    public ResponseEntity<SecretSantaGroupDTO> deleteSecretSantaGroupById(@PathVariable UUID groupId) {
        secretSantaGroupService.deleteSecretSantaGroupById(groupId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Delete a user from a group")
    @DeleteMapping("/{groupId}/deleteUser/{userId}")
    public ResponseEntity<SecretSantaGroupDTO> deleteUserFromSecretSantaGroup(@PathVariable UUID userId, @PathVariable UUID groupId) {
        SecretSantaGroup group = secretSantaGroupService.deleteUserFromSecretSantaGroup(userId, groupId);
        return new ResponseEntity<>(secretSantaGroupMapper.toSecretSantaGroupDTO(group), HttpStatus.OK);
    }


/* Old methods for the posterity, the souvenirs, the good moments ...
    @PostMapping
    public ResponseEntity<SecretSantaGroup> createSecretSantaGroup(@RequestBody SecretSantaGroup secretSantaGroup, Principal principal) {
        Users currentUser = userService.findUserByLogin(principal.getName());
        SecretSantaGroup createdGroup = secretSantaGroupService.createSecretSantaGroup(secretSantaGroup, currentUser.getId());
        return new ResponseEntity<>(createdGroup, HttpStatus.CREATED);
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<SecretSantaGroup> getSecretSantaGroupById(@PathVariable UUID groupId) {
        SecretSantaGroup group = secretSantaGroupService.findSecretSantaGroupById(groupId);
        return new ResponseEntity<>(group, HttpStatus.OK);
    }

    @PutMapping("/{groupId}")
    public ResponseEntity<SecretSantaGroup> updateSecretSantaGroup(@PathVariable UUID groupId, @RequestBody SecretSantaGroup updatedGroup) {
        SecretSantaGroup group = secretSantaGroupService.updateSecretSantaGroup(groupId, updatedGroup);
        return new ResponseEntity<>(group, HttpStatus.OK);
    }

    @DeleteMapping("/{groupId}")
    public ResponseEntity<Void> deleteSecretSantaGroup(@PathVariable UUID groupId) {
        secretSantaGroupService.deleteSecretSantaGroupById(groupId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

 */
}
