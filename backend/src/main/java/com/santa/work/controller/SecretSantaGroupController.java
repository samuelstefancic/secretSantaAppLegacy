package com.santa.work.controller;

import com.santa.work.entity.SecretSantaGroup;
import com.santa.work.entity.Users;
import com.santa.work.service.secretSantaGroup.SecretSantaGroupServiceImpl;
import com.santa.work.service.user.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/api/groups")
@Slf4j
public class SecretSantaGroupController {
    private final SecretSantaGroupServiceImpl secretSantaGroupService;
    private final UserServiceImpl userService;

    @Autowired
    public SecretSantaGroupController(@Lazy SecretSantaGroupServiceImpl secretSantaGroupService, UserServiceImpl userService) {
        this.secretSantaGroupService = secretSantaGroupService;
        this.userService = userService;
    }

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
}
