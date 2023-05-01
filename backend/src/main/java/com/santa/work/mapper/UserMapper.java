package com.santa.work.mapper;

import com.santa.work.dto.InvitationDTO;
import com.santa.work.dto.UserDTO;
import com.santa.work.dto.WishDTO;
import com.santa.work.entity.Invitation;
import com.santa.work.entity.Users;
import com.santa.work.entity.Wish;
import com.santa.work.enumeration.Role;
import com.santa.work.exception.usersExceptions.InvalidUserException;
import com.santa.work.exception.usersExceptions.UserNotFoundException;
import com.santa.work.service.user.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;
@Component
public class UserMapper implements UserMapperDelegate {

    private final SecretSantaGroupMapper secretSantaGroupMapper;

    @Autowired
    public UserMapper(@Lazy SecretSantaGroupMapper secretSantaGroupMapper) {
        this.secretSantaGroupMapper = secretSantaGroupMapper;
    }

    public UserDTO toUserDTO(Users user) {
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }
        if (user.getLogin() == null || user.getPassword() == null) {
            throw new InvalidUserException("User login or pasword is null");
        }
        //Add
        UUID id = user.getId();
        Role role = user.getRole();
        String firstname = user.getFirstname();
        String lastname = user.getLastname();
        String login = user.getLogin();
        String password = user.getPassword();
        List<WishDTO> wishList = WishMapper.toWishDtos(user.getWishList());
        List<UUID> groupIds = secretSantaGroupMapper.toUUIDs(user.getGroups());
        Set<InvitationDTO> invitations = InvitationMapper.toInvitationDTOs(user.getInvitations());
        return new UserDTO(id, role, firstname, lastname, login, password, wishList, groupIds, invitations);
    }
    public Users toUserEntity(UserDTO userDTO) {
        UUID id = userDTO.getId();
        Role role = userDTO.getRole();
        String firstname = userDTO.getFirstname();
        String lastname = userDTO.getLastname();
        String login = userDTO.getLogin();
        String password = userDTO.getPassword();
        Users user = new Users(id, role, firstname, lastname, login, password, new ArrayList<>(), new ArrayList<>(), new HashSet<>());
        List<Wish> wishList = WishMapper.toWishEntities(userDTO.getWishList(), user);
        user.setWishList(wishList);
        //List<SecretSantaGroup> groups = SecretSantaGroupMapper.toSecretSantaGroupEntities(userDTO.getGroupIds(), admin, userService, invitationService, matchService, secretSantaGroupService);
        //user.setGroups(groups);
        Set<Invitation> invitations = InvitationMapper.toInvitationEntities(userDTO.getInvitations());
        user.setInvitations(invitations);
        return user;
    }
    public  List<UUID> toUUIDs(List<Users> users) {
        if (users == null) {
            return Collections.emptyList();
        }
        return users.stream().map(Users::getId).collect(Collectors.toList());
    }

    public  List<Users> toUsers(List<UUID> userIds, UserServiceImpl userService) {
        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyList();
        }
        // Fetch Users from the database using the userIds
        return userService.getAllUsersByIds(userIds);
    }

    public List<UserDTO> toDtoList(List<Users> users) {
        if (users == null) {
            return Collections.emptyList();
        }
        return users.stream().map(this::toUserDTO).collect(Collectors.toList());
    }
}
