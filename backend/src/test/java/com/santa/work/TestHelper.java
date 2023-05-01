package com.santa.work;

import com.github.javafaker.Faker;
import com.santa.work.entity.Invitation;
import com.santa.work.entity.SecretSantaGroup;
import com.santa.work.entity.Users;
import com.santa.work.enumeration.InvitationStatus;
import com.santa.work.enumeration.Role;
import com.santa.work.repository.*;
import com.santa.work.service.secretSantaGroup.SecretSantaGroupServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@SpringBootTest
@Service
public class TestHelper {

    @Autowired
    MatchRepository matchRepository;
    @Autowired
    WishRepository wishRepository;
    @Autowired
    InvitationRepository invitationRepository;

    //SecretSantaGroup
    @Autowired
    private SecretSantaGroupRepository secretSantaGroupRepository;
    @Autowired
    private SecretSantaGroupServiceImpl secretSantaGroupService;

    //Users
    @Autowired
    private  UserRepository userRepository;

    Faker faker = new Faker();

    private Users createUser(boolean isAdmin) {
        Users user = new Users();
        user.setFirstname(faker.name().firstName());
        user.setLastname(faker.name().lastName());
        user.setLogin(faker.name().username());
        user.setPassword(faker.internet().password());
        user.setRole(isAdmin ? Role.ADMIN : Role.USER);
        return userRepository.save(user);
    }

    private SecretSantaGroup createGroup(Users admin) {
        SecretSantaGroup group = new SecretSantaGroup();
        group.setName(faker.lorem().word());
        group = secretSantaGroupService.createSecretSantaGroup(group, admin.getId());
        System.out.println("Generated group URL: " + group.getUrl());
        return group;
    }

    private Invitation createInvitation(Users sender, SecretSantaGroup group, Users receiver) {
        Invitation invitation = new Invitation();
        invitation.setSender(sender);
        invitation.setGroup(group);
        invitation.setEmail(faker.internet().emailAddress());
        invitation.setInvitedUser(receiver);
        invitation.setExpiryDate(LocalDateTime.now().plusDays(7));
        invitation.setStatus(InvitationStatus.PENDING);
        return invitation;
    }
}
