package com.santa.work.controller;

import com.santa.work.dto.SecretSantaGroupDTO;
import com.santa.work.dto.UserDTO;
import com.santa.work.entity.Users;
import com.santa.work.enumeration.Role;
import com.santa.work.mapper.UserMapper;
import com.santa.work.service.secretSantaGroup.SecretSantaGroupServiceImpl;
import com.santa.work.service.user.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@Slf4j
public class UsersController {

    private final UserServiceImpl userService;
    private final SecretSantaGroupServiceImpl secretSantaService;
    private final UserMapper userMapper;

    @Autowired
    public UsersController(UserServiceImpl usersService, SecretSantaGroupServiceImpl secretSantaService,@Lazy UserMapper userMapper) {
        this.userService = usersService;
        this.secretSantaService = secretSantaService;
        this.userMapper = userMapper;
    }

    //DTO
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO) {
           // userDTO.setRole(Role.USER);
            Users users = userMapper.toUserEntity(userDTO);
            Users createdUser = userService.createUser(users);
            UserDTO createdUserDTO = userMapper.toUserDTO(createdUser);
            System.out.println("User with id: " + createdUserDTO.getId() + " created");
            return new ResponseEntity<>(createdUserDTO, HttpStatus.CREATED);
    }
    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable("userId") UUID userId) {
        Users user = userService.findUserById(userId);
        UserDTO userDTO = userMapper.toUserDTO(user);
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }
    @GetMapping("/{userId}/groups")
    public ResponseEntity<List<SecretSantaGroupDTO>> getAllSecretSantaGroupsByUserId(@PathVariable("userId") UUID userId) {
        List<SecretSantaGroupDTO> secretSantaGroupDTOs = userService.findAllSecretSantaGroupById(userId);
        return new ResponseEntity<>(secretSantaGroupDTOs, HttpStatus.OK);
    }
    @Operation(summary = "Get all users")
    @GetMapping("/all")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<Users> users = userService.getAllUsers();
        return new ResponseEntity<>(userMapper.toDtoList(users), HttpStatus.OK);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable("userId") UUID userId, @RequestBody UserDTO updatedUserDTO) {
        Users updatedUser = userMapper.toUserEntity(updatedUserDTO);
        Users user = userService.updateUser(userId, updatedUser);
        UserDTO userDTO = userMapper.toUserDTO(user);
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable("userId") UUID userId) {
        userService.deleteUserById(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }




    /* Old methods, without DTO, for the future, if we need to use them or for the reference or for the tests, or for the future or for the past or
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
*/
}
