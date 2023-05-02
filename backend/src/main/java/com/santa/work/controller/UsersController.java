package com.santa.work.controller;

import com.santa.work.dto.SecretSantaGroupDTO;
import com.santa.work.entity.Users;
import com.santa.work.service.secretSantaGroup.SecretSantaGroupServiceImpl;
import com.santa.work.service.user.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@Slf4j
public class UsersController {

    private final UserServiceImpl userService;
    private final SecretSantaGroupServiceImpl secretSantaService;

    @Autowired
    public UsersController(UserServiceImpl usersService, SecretSantaGroupServiceImpl secretSantaService) {
        this.userService = usersService;
        this.secretSantaService = secretSantaService;
    }

    @PostMapping
    public ResponseEntity<Users> createUser(@RequestBody Users user) {
        Users createdUser = userService.createUser(user);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Users> getUserById(@PathVariable UUID userId) {
        Users user = userService.findUserById(userId);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<Users> updateUser(@PathVariable UUID userId, @RequestBody Users updatedUser) {
        Users user = userService.updateUser(userId, updatedUser);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID userId) {
        userService.deleteUserById(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //DTO

    /* Méthode à modifier et mettre en place
    @GetMapping("/{userId}/groups")
    public ResponseEntity<List<SecretSantaGroupDTO>> getAllSecretSantaGroupsByUserId(@PathVariable("userId") UUID userId) {
        List<SecretSantaGroupDTO> secretSantaGroupDTOs = secretSantaService.findAllSecretSantaGroupsByUserId(userId);
        return new ResponseEntity<>(secretSantaGroupDTOs, HttpStatus.OK);
    }
    */

}
