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
    private final WishMapper wishMapper;
    private final InvitationMapper invitationMapper;

    @Autowired
    public UserMapper(@Lazy SecretSantaGroupMapper secretSantaGroupMapper, @Lazy WishMapper wishMapper, @Lazy InvitationMapper invitationMapper) {
        this.secretSantaGroupMapper = secretSantaGroupMapper;
        this.wishMapper = wishMapper;
        this.invitationMapper = invitationMapper;
    }

    public UserDTO toUserDTO(Users user) {
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }
        if (user.getLogin() == null || user.getPassword() == null) {
            throw new InvalidUserException("User login or pasword is null");
        }
        List<WishDTO> wishList = wishMapper.toWishDtos(user.getWishList());
        List<UUID> groupIds = secretSantaGroupMapper.toUUIDs(user.getGroups());
        Set<InvitationDTO> invitations = invitationMapper.toInvitationDTOs(user.getInvitations());
        System.out.println("User Invitations count: " + user.getInvitations().size());
        return new UserDTO(user.getId(), user.getRole(), user.getFirstname(), user.getLastname(), user.getLogin(), user.getPassword(), user.getEmail(), wishList, groupIds, invitations);
    }
    public Users toUserEntity(UserDTO userDTO) {
        UUID id = userDTO.getId();
        Role role = userDTO.getRole();
        String firstname = userDTO.getFirstname();
        String lastname = userDTO.getLastname();
        String login = userDTO.getLogin();
        String password = userDTO.getPassword();
        String email = userDTO.getEmail();
        Users user = new Users(id, role, firstname, lastname, login, password, email, new ArrayList<>(), new ArrayList<>(), new HashSet<>());
        List<Wish> wishList = wishMapper.toWishEntities(userDTO.getWishList());
        user.setWishList(wishList);
        //List<SecretSantaGroup> groups = SecretSantaGroupMapper.toSecretSantaGroupEntities(userDTO.getGroupIds(), admin, userService, invitationService, matchService, secretSantaGroupService);
        //user.setGroups(groups);
        Set<Invitation> invitations = invitationMapper.toInvitationEntities(userDTO.getInvitations());
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
