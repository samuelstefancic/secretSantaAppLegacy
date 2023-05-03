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

        // Map the DTO to the entity
        Invitation invitation = invitationMapper.toInvitationEntity(invitationDTO);

        // Set group, invited user, and sender
        invitation.setGroup(group);
        invitation.setInvitedUser(invitedUser);
        invitation.setSender(sender);

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
        if (invitationList.isEmpty()) {
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
        invitation.setStatus(InvitationStatus.ACCEPTED);
        SecretSantaGroup group = invitation.getGroup();
        Users invitedUser = invitation.getInvitedUser();
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

    /**
     * Old services related to the invitation
     */

    /*

    public Invitation createInvitation(Invitation invitation) {
        try {
            // Check if the group and invited user exist
            UUID groupId = invitation.getGroup().getId();
            UUID invitedUserId = invitation.getInvitedUser().getId();
            if (!invitationRepository.existsById(groupId) || !invitationRepository.existsById(invitedUserId)) {
                throw new InvitationException("Group or invited user not found", HttpStatus.NOT_FOUND);
            }
            invitation.setStatus(InvitationStatus.PENDING);
            invitation.setExpiryDate(LocalDateTime.now().plusDays(30));
            String token = generateUniqueToken();
            if (token == null || token.isEmpty()) {
                throw new InvitationException("Failed to generate token for the invitation", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            invitation.setToken(token);
            Invitation createdInvitation = invitationRepository.save(invitation);
            if (createdInvitation.getId() == null) {
                throw new InvitationException("failed to create Invitation : invitation id " + invitation.getId() + " is null", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return createdInvitation;
        } catch (DataAccessException | ConstraintViolationException e) {
            throw new InvitationException("failed to create Invitation : " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
*/

}
