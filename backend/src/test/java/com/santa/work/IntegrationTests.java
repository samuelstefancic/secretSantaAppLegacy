package com.santa.work;

import com.github.javafaker.Faker;
import com.santa.work.entity.*;
import com.santa.work.enumeration.InvitationStatus;
import com.santa.work.enumeration.Role;
import com.santa.work.repository.*;
import com.santa.work.service.invitation.InvitationServiceImpl;
import com.santa.work.service.match.MatchService;
import com.santa.work.service.match.MatchServiceImpl;
import com.santa.work.service.secretSantaGroup.SecretSantaGroupServiceImpl;
import com.santa.work.service.user.UserServiceImpl;
import com.santa.work.service.wish.WishServiceImpl;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.transaction.annotation.Propagation;

@SpringBootTest
public class IntegrationTests {

    //User
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserServiceImpl userService;

    //Secret Santa
    @Autowired
    private SecretSantaGroupRepository secretSantaGroupRepository;
    @Autowired
    private SecretSantaGroupServiceImpl secretSantaGroupService;

    //Invitation
    @Autowired
    private InvitationRepository invitationRepository;
    @Autowired
    private InvitationServiceImpl invitationService;

    //Wish
    @Autowired
    private WishRepository wishRepository;
    @Autowired
    private WishServiceImpl wishService;

    //Match
    @Autowired
    private MatchRepository matchRepository;
    @Autowired
    private MatchServiceImpl matchService;

    @Autowired
    private TestHelper testHelper;

    private final Faker faker = new Faker();

    @Test
    public void testIntegration() {
        //Main user, group creator
        Users mainUser = createUser(false);
        System.out.println("mainUser name : " + mainUser.getFirstname());

        //Santa Group
        SecretSantaGroup group = createGroup(mainUser);

        //Users invitation
        // Create and invite users
        Users userInvitedOne = createUser(false);
        Users userInvitedTwo = createUser(false);
        Users userInvitedThree = createUser(false);
        Users userInvitedFour = createUser(false);
        Users userInvitedFive= createUser(false);
        Users userInvitedSix = createUser(false);
        Users userInvitedSeven = createUser(false);

        System.out.println("Member of the group : " + group.getMembers().size());
        List<Users> invitedUsers = Arrays.asList(userInvitedOne, userInvitedTwo, userInvitedThree, userInvitedFour);

        //Match generation

        for (Users invitUser : invitedUsers) {
            Invitation invitation = createInvitation(mainUser, group, invitUser);
            invitationRepository.save(invitation);
        }


        setAllInvitationsToAccepted(group);

        System.out.println("Member of the group : " + group.getMembers().size());
        List<Match> matches = matchService.generateMatchesForGroup(group.getId(), mainUser.getId());

// Validate matches
        assertThat(matches).hasSize(invitedUsers.size() + 1); // The main user is also part of the group
        for (Match match : matches) {
            assertThat(match.getGiverUser().getId()).isNotEqualTo(match.getReceiverUser().getId());
            assertThat(match.getGroup().getId()).isEqualTo(group.getId());
        }

// Check if all users have a match as a giver and a match as a receiver
        List<UUID> userIds = invitedUsers.stream().map(Users::getId).collect(Collectors.toList());
        userIds.add(mainUser.getId());
        for (UUID userId : userIds) {
            assertThat(matches.stream().anyMatch(m -> m.getGiverUser().getId().equals(userId))).isTrue();
            assertThat(matches.stream().anyMatch(m -> m.getReceiverUser().getId().equals(userId))).isTrue();
        }

        //Wishlist

        for(Users user: invitedUsers) {
            Wish wish = createWish(user);
            wishRepository.save(wish);
        }


    }



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
        System.out.println("Admin " + group.getAdmin().getRole() + " first name of the admin : " + group.getAdmin().getFirstname());
        return group;
    }

    private Invitation createInvitation(Users sender, SecretSantaGroup group, Users receiver) {
        Invitation invitation = new Invitation();
        invitation.setSender(sender);
        invitation.setGroup(group);
        invitation.setInvitedUser(receiver);
        invitation.setExpiryDate(LocalDateTime.now().plusDays(7));
        invitation.setStatus(InvitationStatus.PENDING);
        invitation.setEmail(faker.internet().emailAddress());
        invitation.setToken(generateUniqueToken());
        invitationRepository.save(invitation);
        return invitation;
    }

    private Wish createWish(Users user) {
        Wish wish = new Wish();
        wish.setUsers(user);
        wish.setTitle(String.join(" ", faker.lorem().words(3)));
        wish.setDescription(faker.lorem().sentence());
        wish.setUrl(faker.internet().url());
        wishRepository.save(wish);
        return wish;
    }


    public String generateUniqueToken() {
        String token;
        do {
            token = UUID.randomUUID().toString();
        } while (invitationRepository.existsByToken(token));
        return token;
    }

    private void setAllInvitationsToAccepted(SecretSantaGroup group) {
        List<Invitation> invitations = invitationRepository.findByGroup(group);
        for (Invitation invitation : invitations) {
            // Call acceptInvitation() method
            Invitation acceptedInvitation = invitationService.acceptInvitation(invitation.getId());

            /*// Assertions to make sure the invitation has been accepted
            assertThat(acceptedInvitation.getStatus()).isEqualTo(InvitationStatus.ACCEPTED);
            assertThat(acceptedInvitation.getGroup().getMembers()).contains(acceptedInvitation.getInvitedUser());
            assertThat(acceptedInvitation.getInvitedUser().getGroups()).contains(acceptedInvitation.getGroup());*/

            System.out.println("Group members count: " + acceptedInvitation.getGroup().getMembers().size());
        }
    }


}
