package com.santa.work.service.user;

import com.santa.work.dto.SecretSantaGroupDTO;
import com.santa.work.dto.UserDTO;
import com.santa.work.entity.SecretSantaGroup;
import com.santa.work.entity.Users;
import com.santa.work.entity.Wish;
import com.santa.work.exception.usersExceptions.UsersException;
import com.santa.work.mapper.SecretSantaGroupMapper;
import com.santa.work.mapper.UserMapper;
import com.santa.work.mapper.WishMapper;
import com.santa.work.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Transactional
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final WishMapper wishMapper;
    private final SecretSantaGroupMapper secretSantaGroupMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, WishMapper wishMapper,@Lazy SecretSantaGroupMapper secretSantaGroupMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.wishMapper = wishMapper;
        this.secretSantaGroupMapper = secretSantaGroupMapper;
    }

    public UserDTO createUserDTO(UserDTO userDTO) {
        try {
            Users user = userMapper.toUserEntity(userDTO);
            Users createdUser = userRepository.save(user);
            if (Objects.equals(createdUser.getId(), null)) {
                throw new UsersException("failed to create User, id is null", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return userMapper.toUserDTO(createdUser);
        } catch (DataAccessException | ConstraintViolationException e) {
            throw new UsersException("failed to create User " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //Find

    public UserDTO findUserByIdWithDTO(UUID userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UsersException("User with id " + userId + " not found", HttpStatus.NOT_FOUND));
        return userMapper.toUserDTO(user);
    }


    public List<SecretSantaGroupDTO> findAllSecretSantaGroupById(UUID userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UsersException("User with id " + userId + " not found", HttpStatus.NOT_FOUND));
        List<SecretSantaGroup> secretSantaGroup = user.getGroups();
        List<SecretSantaGroupDTO> secretSantaGroupDTO = secretSantaGroupMapper.toSecretSantaGroupDTOs(secretSantaGroup);
        return secretSantaGroupDTO;
    }

    public UserDTO findUserByLoginWithDTO(String login) {
        Users user = userRepository.findByLogin(login)
                .orElseThrow(() -> new UsersException("User with email " + login + " not found", HttpStatus.NOT_FOUND));
        return userMapper.toUserDTO(user);
    }

    public UserDTO findUserByFirstNameAndLastNameAndIdWithDTO(String firstname, String lastname, UUID id) {
        if (!userRepository.existsById(id)) {
            throw new UsersException("User with ID : " + id + " not found", HttpStatus.NOT_FOUND);
        }
        Users user = userRepository.findByFirstnameAndLastnameAndId(firstname, lastname, id);
        return userMapper.toUserDTO(user);
    }

    public List<Users> findUsersById(List<UUID> usersIds) {
        if (usersIds == null || usersIds.isEmpty()) {
            return Collections.emptyList();
        }
        return userRepository.findAllById(usersIds);
    }

    //Update
    public UserDTO updateUserWithDTO(UUID id, UserDTO updatedUserDTO) {
        Users updatedUser = userMapper.toUserEntity(updatedUserDTO);
        Users user = userRepository.findById(id).orElseThrow(() -> new UsersException("User with Id " + id + " not found", HttpStatus.NOT_FOUND));
        // Simple update entity
        user.setFirstname(updatedUser.getFirstname());
        user.setLastname(updatedUser.getLastname());
        user.setLogin(updatedUser.getLogin());
        user.setPassword(updatedUser.getPassword());
        // WishList update
        // Adding a logic to delete the existing wishes that are not in the list anymore
        user.getWishList().removeIf(wish -> !updatedUser.getWishList().contains(wish));
        // Add or update the wishlist
        for (Wish updatedWish : updatedUser.getWishList()) {
            Optional<Wish> existWishOpt = user.getWishList().stream().filter(wish -> wish.getId().equals(updatedWish.getId())).findFirst();
            if (existWishOpt.isPresent()) {
                // update wish
                Wish isExistWish = existWishOpt.get();
                isExistWish.setTitle(updatedWish.getTitle());
                isExistWish.setDescription(updatedWish.getDescription());
                isExistWish.setUrl(updatedWish.getUrl());
            } else {
                updatedWish.setUsers(user);
                user.getWishList().add(updatedWish);
            }
        }
        Users savedUser = userRepository.save(user);
        return userMapper.toUserDTO(savedUser);
    }

    public void deleteUserByIdWithDTO(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new UsersException("UserDTO with ID : " + userId + " not found", HttpStatus.NOT_FOUND);
        }
        userRepository.deleteById(userId);
    }

    public List<UserDTO> getAllUsersWithDTO() {
        List<Users> users = userRepository.findAll();
        return userMapper.toDtoList(users);
    }

    public List<UserDTO> getAllUsersByIdsWithDTO(List<UUID> ids) {
        List<Users> users = userRepository.findAllById(ids);
        return userMapper.toDtoList(users);
    }

    //Old methods, which are not used anymore due to the DTO mapping

    public Users createUser(Users user) {
       try {
           Users createdUser = userRepository.save(user);
           if (Objects.equals(createdUser.getId(), null)) {
               throw new UsersException("failed to create User, id is null", HttpStatus.INTERNAL_SERVER_ERROR);
           }
           return createdUser;
       } catch (DataAccessException | ConstraintViolationException e) {
           throw new UsersException("failed to create User " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
       }
    }

    public Users findUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UsersException("User with id " + userId + " not found", HttpStatus.NOT_FOUND));
    }


    public Users findUserByLogin(String login) {
        return userRepository.findByLogin(login)
                .orElseThrow(() -> new UsersException("User with email " + login + " not found", HttpStatus.NOT_FOUND));
    }

    public Users findUserByFirstNameAndLastNameAndId(String firstname, String lastname, UUID id) {
        if (!userRepository.existsById(id)) {
            throw new UsersException("User with ID : " + id + " not found", HttpStatus.NOT_FOUND);
        }
        return userRepository.findByFirstnameAndLastnameAndId(firstname, lastname, id);
    }


    public Users updateUser(UUID id, Users updatedUser) {
        Users user = userRepository.findById(id).orElseThrow(() -> new UsersException("User with Id " + id + " not found", HttpStatus.NOT_FOUND));
        //Simple update entity
        user.setFirstname(updatedUser.getFirstname());
        user.setLastname(updatedUser.getLastname());
        user.setLogin(updatedUser.getLogin());
        user.setPassword(updatedUser.getPassword());
        //WishList update
        //Adding a logic to delete the existing wishes that are not in the list anymore
        user.getWishList().removeIf(wish -> !updatedUser.getWishList().contains(wish));
        //Add or update the wishlist
        for (Wish updatedWish : updatedUser.getWishList()) {
            Optional<Wish> existWishOpt = user.getWishList().stream().filter(wish -> wish.getId().equals(updatedWish.getId())).findFirst();
            if (existWishOpt.isPresent()) {
                //update wish
                Wish isExistWish = existWishOpt.get();
                isExistWish.setTitle(updatedWish.getTitle());
                isExistWish.setDescription(updatedWish.getDescription());
                isExistWish.setUrl(updatedWish.getUrl());
            } else {
                updatedWish.setUsers(user);
                user.getWishList().add(updatedWish);
            }
        }
        return userRepository.save(user);
    }


    public void deleteUserById(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new UsersException("User with ID : " + id + " not found", HttpStatus.NOT_FOUND);
        }
        try {
            userRepository.deleteById(id);
        } catch (Exception e){
            throw new UsersException("Failed to delete user with id : " + id + " ", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    public List<Users> getAllUsers() {
        return userRepository.findAll();
    }

    public List<Users> getAllUsersByIds(List<UUID> userIds) {
        return userRepository.findAllById(userIds);
    }
}
