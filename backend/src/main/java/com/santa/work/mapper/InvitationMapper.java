package com.santa.work.mapper;

import com.santa.work.dto.InvitationDTO;
import com.santa.work.entity.Invitation;
import com.santa.work.entity.SecretSantaGroup;
import com.santa.work.entity.Users;
import com.santa.work.enumeration.InvitationStatus;
import com.santa.work.service.invitation.InvitationServiceImpl;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class InvitationMapper {
    public InvitationDTO toInvitationDTO(Invitation invitation) {
        if (invitation == null) {
            return null;
        }
        UUID id = invitation.getId();
        String invitationName = invitation.getInvitationName();
        String email = invitation.getEmail();
        String token = invitation.getToken();
        InvitationStatus invitationStatus = invitation.getStatus();
        LocalDateTime expiryDate = invitation.getExpiryDate();
        UUID groupId = invitation.getGroup().getId();
        UUID invitedUserId = invitation.getInvitedUser().getId();
        UUID senderId = invitation.getSender().getId();
        String groupUrl = invitation.getGroupUrl();
        System.out.println("InvitationMapper.toInvitationDTO: groupUrl = " + groupUrl);
        return new InvitationDTO(id, invitationName, email, token,groupUrl, invitationStatus, expiryDate, groupId, invitedUserId, senderId);
    }
    public Set<InvitationDTO> toInvitationDTOs(Set<Invitation> invitations) {
        if (invitations == null) {
            return Collections.emptySet();
        }
        Set <InvitationDTO> invitationDTOs = invitations.stream().map(this::toInvitationDTO).collect(Collectors.toSet());
        return invitationDTOs;
    }
    public Invitation toInvitationEntity(InvitationDTO invitationDTO) {
        if (invitationDTO == null) {
            return null;
        }
        UUID id = invitationDTO.getId();
        String invitationName = invitationDTO.getInvitationName();
        String email = invitationDTO.getEmail();
        String token = invitationDTO.getToken();
        InvitationStatus invitationStatus = invitationDTO.getInvitationStatus();
        LocalDateTime expiryDate = invitationDTO.getExpiryDate();
        Invitation invitation = new Invitation();
        invitation.setId(id);
        invitation.setInvitationName(invitationName);
        invitation.setEmail(email);
        invitation.setToken(token);
        invitation.setStatus(invitationStatus);
        invitation.setExpiryDate(expiryDate);
        invitation.setGroupUrl(invitationDTO.getGroupUrl());
        return invitation;
    }
    public Set<Invitation> toInvitationEntities(Set<InvitationDTO> invitationDTOs) {
        if (invitationDTOs == null) {
            return Collections.emptySet();
        }
        return invitationDTOs.stream()
                .map(invitationDTO -> {
                    Invitation invitation = new Invitation();
                    invitation.setId(invitationDTO.getId());
                    invitation.setEmail(invitationDTO.getEmail());
                    invitation.setToken(invitationDTO.getToken());
                    invitation.setStatus(invitationDTO.getInvitationStatus());
                    invitation.setExpiryDate(invitationDTO.getExpiryDate());
                    return invitation;
                })
                .collect(Collectors.toSet());
    }
    public List<UUID> toUUIDs(List<Invitation> invitations) {
        if (invitations == null) {
            return Collections.emptyList();
        }
        return invitations.stream().map(Invitation::getId).collect(Collectors.toList());
    }
    public List<Invitation> toInvitations(List<UUID> invitationIds, InvitationServiceImpl invitationService) {
        if (invitationIds == null || invitationIds.isEmpty()) {
            return Collections.emptyList();
        }
        //Fetch invitation from database with invitationIds
        return invitationService.getAllInvitationsByIds(invitationIds);
    }
}