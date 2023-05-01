package com.santa.work.DTOTests;

import com.santa.work.dto.*;
import com.santa.work.enumeration.InvitationStatus;
import com.santa.work.enumeration.Role;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class DTOTests {
    @Test
    public void testInvitationDTO() {
        InvitationDTO invitationDTO = new InvitationDTO();
        UUID id = UUID.randomUUID();
        String email = "test@example.com";
        String token = "test-token";
        InvitationStatus invitationStatus = InvitationStatus.PENDING;
        LocalDateTime expiryDate = LocalDateTime.now();
        UUID groupId = UUID.randomUUID();
        UUID invitedUserId = UUID.randomUUID();
        UUID senderId = UUID.randomUUID();

        invitationDTO.setId(id);
        invitationDTO.setEmail(email);
        invitationDTO.setToken(token);
        invitationDTO.setInvitationStatus(invitationStatus);
        invitationDTO.setExpiryDate(expiryDate);
        invitationDTO.setGroupId(groupId);
        invitationDTO.setInvitedUserId(invitedUserId);
        invitationDTO.setSenderId(senderId);

        assertEquals(id, invitationDTO.getId());
        assertEquals(email, invitationDTO.getEmail());
        assertEquals(token, invitationDTO.getToken());
        assertEquals(invitationStatus, invitationDTO.getInvitationStatus());
        assertEquals(expiryDate, invitationDTO.getExpiryDate());
        assertEquals(groupId, invitationDTO.getGroupId());
        assertEquals(invitedUserId, invitationDTO.getInvitedUserId());
        assertEquals(senderId, invitationDTO.getSenderId());
    }
    @Test
    public void testMatchDTO() {
        MatchDTO matchDTO = new MatchDTO();
        UUID id = UUID.randomUUID();
        UUID giverUserId = UUID.randomUUID();
        UUID receiverUserId = UUID.randomUUID();
        boolean isRevealed = true;
        UUID groupId = UUID.randomUUID();

        matchDTO.setId(id);
        matchDTO.setGiverUserId(giverUserId);
        matchDTO.setReceiverUserId(receiverUserId);
        matchDTO.setRevealed(isRevealed);
        matchDTO.setGroupId(groupId);

        assertEquals(id, matchDTO.getId());
        assertEquals(giverUserId, matchDTO.getGiverUserId());
        assertEquals(receiverUserId, matchDTO.getReceiverUserId());
        assertEquals(isRevealed, matchDTO.isRevealed());
        assertEquals(groupId, matchDTO.getGroupId());
    }

    @Test
    public void testSecretSantaGroupDTO() {
        SecretSantaGroupDTO secretSantaGroupDTO = new SecretSantaGroupDTO();
        UUID id = UUID.randomUUID();
        String name = "Test Group";
        UUID adminId = UUID.randomUUID();
        String url = "https://example.com/group";
        boolean matchesGenerated = true;
        List<UUID> memberIds = Arrays.asList(UUID.randomUUID(), UUID.randomUUID());
        List<UUID> invitationIds = Arrays.asList(UUID.randomUUID(), UUID.randomUUID());
        List<UUID> matchIds = Arrays.asList(UUID.randomUUID(), UUID.randomUUID());

        secretSantaGroupDTO.setId(id);
        secretSantaGroupDTO.setName(name);
        secretSantaGroupDTO.setAdminId(adminId);
        secretSantaGroupDTO.setUrl(url);
        secretSantaGroupDTO.setMatchesGenerated(matchesGenerated);
        secretSantaGroupDTO.setMemberIds(memberIds);
        secretSantaGroupDTO.setInvitationIds(invitationIds);
        secretSantaGroupDTO.setMatchIds(matchIds);

        assertEquals(id, secretSantaGroupDTO.getId());
        assertEquals(name, secretSantaGroupDTO.getName());
        assertEquals(adminId, secretSantaGroupDTO.getAdminId());
        assertEquals(url, secretSantaGroupDTO.getUrl());
        assertEquals(matchesGenerated, secretSantaGroupDTO.isMatchesGenerated());
        assertEquals(memberIds, secretSantaGroupDTO.getMemberIds());
        assertEquals(invitationIds, secretSantaGroupDTO.getInvitationIds());
        assertEquals(matchIds, secretSantaGroupDTO.getMatchIds());
    }
    @Test
    public void testUserDTO() {
        UserDTO userDTO = new UserDTO();
        UUID id = UUID.randomUUID();
        Role role = Role.ADMIN;
        String firstname = "John";
        String lastname = "Doe";
        String login = "johndoe";
        String password = "password";
        List<WishDTO> wishList = Arrays.asList(new WishDTO(), new WishDTO());
        List<UUID> groupIds = Arrays.asList(UUID.randomUUID(), UUID.randomUUID());
        Set<InvitationDTO> invitations = new HashSet<>(Arrays.asList(new InvitationDTO(), new InvitationDTO()));

        userDTO.setId(id);
        userDTO.setRole(role);
        userDTO.setFirstname(firstname);
        userDTO.setLastname(lastname);
        userDTO.setLogin(login);
        userDTO.setPassword(password);
        userDTO.setWishList(wishList);
        userDTO.setGroupIds(groupIds);
        userDTO.setInvitations(invitations);

        assertEquals(id, userDTO.getId());
        assertEquals(role, userDTO.getRole());
        assertEquals(firstname, userDTO.getFirstname());
        assertEquals(lastname, userDTO.getLastname());
        assertEquals(login, userDTO.getLogin());
        assertEquals(password, userDTO.getPassword());
        assertEquals(wishList, userDTO.getWishList());
        assertEquals(groupIds, userDTO.getGroupIds());
        assertEquals(invitations, userDTO.getInvitations());
    }

    @Test
    public void testWishDTO() {
        WishDTO wishDTO = new WishDTO();
        UUID id = UUID.randomUUID();
        String title = "a title";
        String description = "A new phone";
        UUID userId = UUID.randomUUID();
        String url = "A new url";

        wishDTO.setId(id);
        wishDTO.setDescription(description);
        wishDTO.setTitle(title);
        wishDTO.setUrl(url);
        wishDTO.setUserId(userId);

        assertEquals(id, wishDTO.getId());
        assertEquals(description, wishDTO.getDescription());
        assertEquals(url, wishDTO.getUrl());
        assertEquals(userId, wishDTO.getUserId());
        assertEquals(title, wishDTO.getTitle());
    }
 }
