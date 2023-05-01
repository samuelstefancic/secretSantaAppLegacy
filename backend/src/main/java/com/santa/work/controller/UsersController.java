package com.santa.work.controller;

import com.santa.work.entity.Users;
import com.santa.work.service.user.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/users")
@Slf4j
public class UsersController {

    private final UserServiceImpl userService;

    @Autowired
    public UsersController(UserServiceImpl usersService) {
        this.userService = usersService;
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
}
