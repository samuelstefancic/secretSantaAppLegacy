package com.santa.work.DTOTests;

import com.github.javafaker.Faker;
import com.santa.work.dto.InvitationDTO;
import com.santa.work.entity.Invitation;
import com.santa.work.entity.SecretSantaGroup;
import com.santa.work.entity.Users;
import com.santa.work.enumeration.InvitationStatus;
import com.santa.work.enumeration.Role;
import com.santa.work.mapper.InvitationMapper;
import com.santa.work.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

@SpringBootTest
public class InvitationMapperTest {

    Faker faker = new Faker();

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    public InvitationMapperTest(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Test
    public void testMappingBetweenInvitationAndInvitationDTO() {
        // Prepare data
        //Users
        Users invitedUser1 = createUser();
         Users invitedUser2 = createUser();
         Users invitedUser3 = createUser();
         Users invitedUser4 = createUser();

         //SecretSantaGroup
        SecretSantaGroup group = createSecretSantaGroupEntity();

        //Invitation
        Invitation invitation = new Invitation();
        invitation.setEmail(faker.internet().emailAddress());
        invitation.setToken(faker.internet().password());
        invitation.setStatus(InvitationStatus.PENDING);
        invitation.setExpiryDate(LocalDateTime.now().plusDays(7));
        invitation.setGroup(group);
        invitation.setInvitedUser(invitedUser1);
        invitation.setInvitedUser(invitedUser2);
        invitation.setInvitedUser(invitedUser3);
        invitation.setInvitedUser(invitedUser4);
        System.out.println("Invitation: " + invitation);
        System.out.println("Invitation: " + invitation.getInvitedUser());
        invitation.setInvitedUser(invitedUser1);
        invitation.setSender(invitedUser2);

        InvitationDTO invitationDTO = InvitationMapper.toInvitationDTO(invitation);

        assertEquals(invitation.getId(), invitationDTO.getId());
        assertEquals(invitation.getEmail(), invitationDTO.getEmail());
        assertEquals(invitation.getToken(), invitationDTO.getToken());
        assertEquals(invitation.getStatus(), invitationDTO.getInvitationStatus());
        assertEquals(invitation.getExpiryDate(), invitationDTO.getExpiryDate());
        assertEquals(invitation.getGroup().getId(), invitationDTO.getGroupId());
        assertEquals(invitation.getInvitedUser().getId(), invitationDTO.getInvitedUserId());
        assertEquals(invitation.getSender().getId(), invitationDTO.getSenderId());

    }
    private Users createUser() {
        Users user = new Users();
        user.setFirstname(faker.name().firstName());
        user.setLastname(faker.name().lastName());
        user.setLogin(faker.name().username());
        user.setPassword(faker.internet().password());
        user.setRole(true ? Role.ADMIN : Role.USER);
        return userRepository.save(user);
    }


    private SecretSantaGroup createSecretSantaGroupEntity() {
        SecretSantaGroup group = new SecretSantaGroup();
        group.setName(faker.name().bloodGroup());
        group.setAdmin(createUser());
        group.setUrl(faker.internet().url());
        group.setMatchesGenerated(false);
        return group;
    }
}