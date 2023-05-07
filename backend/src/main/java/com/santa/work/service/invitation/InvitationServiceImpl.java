package com.santa.work.service.invitation;

import com.santa.work.dto.InvitationDTO;
import com.santa.work.entity.Invitation;
import com.santa.work.entity.SecretSantaGroup;
import com.santa.work.entity.Users;
import com.santa.work.enumeration.InvitationStatus;
import com.santa.work.exception.invitationExceptions.InvitationException;
import com.santa.work.exception.secretSantaGroupExceptions.SecretSantaGroupNotFoundException;
import com.santa.work.exception.usersExceptions.UserNotFoundException;
import com.santa.work.mapper.InvitationMapper;
import com.santa.work.repository.InvitationRepository;
import com.santa.work.repository.SecretSantaGroupRepository;
import com.santa.work.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class InvitationServiceImpl implements InvitationService{

    private final InvitationRepository invitationRepository;
    private final UserRepository userRepository;
    private final SecretSantaGroupRepository secretSantaGroupRepository;
    private final InvitationMapper invitationMapper;
    @Autowired
    public InvitationServiceImpl(InvitationRepository invitationRepository, UserRepository userRepository, SecretSantaGroupRepository secretSantaGroupRepository,@Lazy InvitationMapper invitationMapper) {
        this.invitationRepository = invitationRepository;
        this.userRepository = userRepository;
        this.secretSantaGroupRepository = secretSantaGroupRepository;
        this.invitationMapper = invitationMapper;
    }

    public Invitation createInvitation(InvitationDTO invitationDTO, UUID groupId, UUID senderId) {

        // Check if the group and invited user exist
        SecretSantaGroup group = secretSantaGroupRepository.findById(groupId).
                orElseThrow(() -> new SecretSantaGroupNotFoundException("The groupd with id" + groupId + " not found"));
        Users invitedUser = userRepository.findByEmail(invitationDTO.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User with email " + invitationDTO.getEmail() + " not found"));
        Users sender = userRepository.findById(senderId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + senderId + " not found"));

        if (isUserInGroup(invitedUser, group)) {
            throw new InvitationException("User with email " + invitedUser.getEmail() + " is already in the group", HttpStatus.BAD_REQUEST);
        }
        if (hasUserAlreadyReceivedInvitationForGroup(invitedUser, groupId)) {
            throw new InvitationException("User with email " + invitedUser.getEmail() + " has already received an invitation for this group", HttpStatus.BAD_REQUEST);
        }
        if (!canSendInvitation(group)) {
            throw new InvitationException("Group with id " + groupId + " has reached the maximum number of invitations", HttpStatus.BAD_REQUEST);
        }

        String groupUrl = generateUniqueGroupUrl(groupId);
        invitationDTO.setGroupUrl(groupUrl);

        // Map the DTO to the entity
        Invitation invitation = invitationMapper.toInvitationEntity(invitationDTO);

        // Set group, invited user, and sender
        invitation.setGroup(group);
        invitation.setInvitedUser(invitedUser);
        invitation.setSender(sender);
        invitation.setGroupUrl(groupUrl);

        // Set additional fields
        invitation.setStatus(InvitationStatus.PENDING);
        invitation.setExpiryDate(LocalDateTime.now().plusDays(30));
        String token = generateUniqueToken();
        if (token == null || token.isEmpty()) {
            throw new InvitationException("Failed to generate token for the invitation", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        invitation.setToken(token);

        // Save the invitation
        try {
            invitedUser.setInvitations(Collections.singleton(invitation));
            Invitation createdInvitation = invitationRepository.save(invitation);
            if (createdInvitation.getId() == null) {
                throw new InvitationException("Failed to create Invitation: invitation id " + invitation.getId() + " is null", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return createdInvitation;
        } catch (DataAccessException | ConstraintViolationException e) {
            throw new InvitationException("Failed to create Invitation: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Invitation findInvitationById(UUID invitationId) {
        return invitationRepository.findById(invitationId)
                .orElseThrow(() -> new InvitationException("Invitation with id " + invitationId + " not found", HttpStatus.NOT_FOUND));
    }

    public List<Invitation> findInvitationsByIds(List<UUID> invitationIds) {
        if (invitationIds == null || invitationIds.isEmpty()) {
            return Collections.emptyList();
        }
        return invitationRepository.findAllById(invitationIds);
    }

    public Invitation updateInvitation(Invitation updatedInvitation, UUID id) {
        Invitation invitation = invitationRepository.findById(id)
                .orElseThrow(() -> new InvitationException("The invitation with ID : " + id + " does not exist"
                ,HttpStatus.NOT_FOUND));
        invitation.setGroup(updatedInvitation.getGroup());
        invitation.setToken(updatedInvitation.getToken());
        invitation.setExpiryDate(updatedInvitation.getExpiryDate());
        invitation.setEmail(updatedInvitation.getEmail());
        return invitationRepository.save(invitation);
    }

    public void deleteInvitationById(UUID id) {
        if (!invitationRepository.existsById(id)) {
            throw new InvitationException("Invitation with ID : " + id + " not found", HttpStatus.NOT_FOUND);
        }
        try {
            invitationRepository.deleteById(id);
        } catch (Exception e) {
            throw new InvitationException("Failed to delete invitation with ID " + id + " ", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Set<Invitation> getAllInvitations() {
        List<Invitation> invitationList = invitationRepository.findAll();
        if (invitationList.isEmpty() || invitationList == null) {
            return Collections.emptySet();
        }
        return new HashSet<>(invitationList);
    }

    //Accept an invitation

    public Invitation acceptInvitation(UUID invitationId) {
        Invitation invitation = findInvitationById(invitationId);
        if (invitation.getStatus() != InvitationStatus.PENDING) {
            throw new InvitationException("Cannot accept an invitation that is not pending", HttpStatus.BAD_REQUEST);
        }
        if (isInvitationExpired(invitation)) {
            throw new InvitationException("Cannot accept an invitation that has expired", HttpStatus.BAD_REQUEST);
        }
        SecretSantaGroup group = invitation.getGroup();
        Users invitedUser = invitation.getInvitedUser();

        if (isUserAlreadyInGroup(invitedUser, group)) {
            throw new InvitationException("User with email " + invitedUser.getEmail() + " is already in the group", HttpStatus.BAD_REQUEST);
        }
        invitation.setStatus(InvitationStatus.ACCEPTED);
        group.getMembers().add(invitedUser);
        secretSantaGroupRepository.save(group);
        return invitationRepository.save(invitation);
    }

    //Refuse an invitation

    public Invitation declineInvitation(UUID invitationId) {
        Invitation invitation = findInvitationById(invitationId);
        if (invitation.getStatus() != InvitationStatus.PENDING) {
            throw new InvitationException("Cannot decline an invitation that is not pending", HttpStatus.BAD_REQUEST);
        }
        invitation.setStatus(InvitationStatus.DECLINED);
        return invitationRepository.save(invitation);
    }
    public Invitation extendExpiryDate(UUID invitationId) {
        Invitation invitation = findInvitationById(invitationId);
        if (invitation.getStatus() != InvitationStatus.PENDING) {
            throw new InvitationException("Cannot extend the expiry date of an invitation that is not pending", HttpStatus.BAD_REQUEST);
        }
        invitation.setExpiryDate(invitation.getExpiryDate().plusDays(30));
        return invitationRepository.save(invitation);
    }
    public String generateUniqueToken() {
        String token;
        do {
            token = UUID.randomUUID().toString();
        } while (invitationRepository.existsByToken(token));
        return token;
    }

    public List<Invitation> getAllInvitationsByIds(List<UUID> invitationsIds) {return invitationRepository.findAllById(invitationsIds);}

    private boolean isUserInGroup(Users user, SecretSantaGroup group) {
        return group.getMembers().contains(user);
    }

    private boolean isUserAlreadyInGroup(Users user, SecretSantaGroup group) {
        return group.getMembers().contains(user);
    }

    private boolean canSendInvitation(SecretSantaGroup group) {
        long invitationCount = invitationRepository.countByGroup(group);
        return invitationCount < 1000;
    }

    private boolean isInvitationExpired(Invitation invitation) {
        return LocalDateTime.now().isAfter(invitation.getExpiryDate());
    }


    public String generateUniqueGroupUrl(UUID groupId) {
        String groupIdUrlSafe = groupId.toString().substring(0, 6);
        String uniqueUrl;

        do {
            uniqueUrl = groupIdUrlSafe + generateRandomAlphanumericString(4);
        } while (urlExistsInDatabase(uniqueUrl));

        return uniqueUrl;
    }

    private String generateRandomAlphanumericString(int length) {
        StringBuilder sb = new StringBuilder(length);
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * characters.length());
            sb.append(characters.charAt(index));
        }

        return sb.toString();
    }


    private String encryptGroupName(String groupName) {
       Random random = new Random();
       StringBuilder encryptedGroupName = new StringBuilder();
       for (char c : groupName.toCharArray()) {
           int newChar = random.nextInt(62);
           if (newChar < 10) {
               encryptedGroupName.append(newChar);
           } else if (newChar < 36) {
               encryptedGroupName.append((char) ('a' + newChar - 10));
           } else {
               encryptedGroupName.append((char) ('A' + newChar - 36));
           }
       }
       return encryptedGroupName.toString();
    }

    private String mixUrlParts(String encryptedInvitationName, String uniqueIdentifier, String groupId) {
        encryptedInvitationName = shuffleText(encryptedInvitationName);
        uniqueIdentifier = shuffleText(uniqueIdentifier);
        groupId = shuffleText(groupId);

        List<String> parts = new ArrayList<>(Arrays.asList(encryptedInvitationName, uniqueIdentifier, groupId));
        Collections.shuffle(parts);
        return String.join("", parts);
    }

    private String shuffleText(String text) {
        List<String> parts = new ArrayList<>(Arrays.asList(text.split("")));
        Collections.shuffle(parts);
        return String.join("", parts);
    }

    private boolean isUrlValid(String url) {
        boolean hasUpperCase = !url.equals(url.toLowerCase());
        boolean hasLowerCase = !url.equals(url.toUpperCase());
        boolean hasDigit = url.chars().anyMatch(Character::isDigit);
        return hasUpperCase && hasLowerCase && hasDigit;
    }

    private boolean urlExistsInDatabase(String url) {
        return invitationRepository.findByGroupUrl(url).isPresent();
    }

    private boolean hasUserAlreadyReceivedInvitationForGroup(Users user, UUID groupId) {
        return user.getInvitations().stream().anyMatch(invitation -> invitation.getGroup().getId().equals(groupId));
    }
}
