package com.santa.work.service.user;

import com.santa.work.dto.UserDTO;
import com.santa.work.entity.Users;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
@Service
public interface UserService {
    Users createUser(Users user);
    Users findUserById(UUID userId);
    Users findUserByLogin(String email);

    Users findUserByFirstNameAndLastNameAndId(String firstname, String lastname, UUID id);
    Users updateUser(UUID id, Users updatedUser);
    void deleteUserById(UUID id);
    List<Users> getAllUsers();

    //DTO

    UserDTO createUserDTO(UserDTO userDTO);
    UserDTO findUserByIdWithDTO(UUID userId);
}
