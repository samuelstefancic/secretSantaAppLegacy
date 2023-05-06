package com.santa.work.controller;

import com.santa.work.dto.InvitationDTO;
import com.santa.work.entity.Invitation;
import com.santa.work.mapper.InvitationMapper;
import com.santa.work.service.invitation.InvitationServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/invitations")
@Slf4j
public class InvitationController {
    private final InvitationServiceImpl invitationService;
    private final InvitationMapper invitationMapper;

    @Autowired
    public InvitationController(InvitationServiceImpl invitationService, @Lazy InvitationMapper invitationMapper){this.invitationService = invitationService;
        this.invitationMapper = invitationMapper;
    }

    @Operation(summary = "Create an invitation")
    @PostMapping("/{groupId}/{senderId}")
    public ResponseEntity<InvitationDTO> createInvitation(@RequestBody InvitationDTO invitationDTO, @PathVariable UUID groupId, @PathVariable UUID senderId) {
        Invitation invitation = invitationService.createInvitation(invitationDTO, groupId, senderId);
        System.out.println(invitation.getGroupUrl() + invitation.getInvitationName());
        return new ResponseEntity<>(invitationMapper.toInvitationDTO(invitation), HttpStatus.CREATED);
    }

    @Operation(summary = "Get all invitations")
    @GetMapping("/all")
    public ResponseEntity<Set<InvitationDTO>> getAllInvitations() {
        Set<Invitation> invitations = invitationService.getAllInvitations();
        Set<InvitationDTO> invitationDTOS = invitationMapper.toInvitationDTOs(invitations);
        return new ResponseEntity<>(invitationDTOS, HttpStatus.OK);
    }

    @Operation(summary = "Get an invitation by id")
    @GetMapping("/{invitationId}")
    public ResponseEntity<InvitationDTO> getInvitationById(@PathVariable UUID invitationId) {
        Invitation invitation = invitationService.findInvitationById(invitationId);
        return new ResponseEntity<>(invitationMapper.toInvitationDTO(invitation), HttpStatus.OK);
    }

    @Operation(summary = "Update an invitation")
    @PutMapping("/{invitationId}")
    public ResponseEntity<InvitationDTO> updateInvitation(@RequestBody InvitationDTO updatedInvitationDTO, @PathVariable UUID invitationId) {
        Invitation updatedInvitation = invitationMapper.toInvitationEntity(updatedInvitationDTO);
        Invitation invitation = invitationService.updateInvitation(updatedInvitation, invitationId);
        return new ResponseEntity<>(invitationMapper.toInvitationDTO(invitation), HttpStatus.OK);
    }

    @Operation(summary = "Delete an invitation")
    @DeleteMapping("/{invitationId}")
    public ResponseEntity<Void> deleteInvitation(@PathVariable UUID invitationId) {
        invitationService.deleteInvitationById(invitationId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Accept the invitation")
    @PostMapping("/accept/{invitationId}")
    public ResponseEntity<InvitationDTO> acceptInvitation(@PathVariable UUID invitationId) {
        Invitation invitation = invitationService.acceptInvitation(invitationId);
        return new ResponseEntity<>(invitationMapper.toInvitationDTO(invitation), HttpStatus.OK);
    }

    @Operation(summary = "Decline the invitation")
    @PostMapping("/decline/{invitationId}")
    public ResponseEntity<InvitationDTO> declineInvitation(@PathVariable UUID invitationId) {
        Invitation invitation = invitationService.declineInvitation(invitationId);
        return new ResponseEntity<>(invitationMapper.toInvitationDTO(invitation), HttpStatus.OK);
    }

    @Operation(summary = "Extend the expiry date of the invitation")
    @PostMapping("/extend/{invitationId}")
    public ResponseEntity<InvitationDTO> extendExpiryDate(@PathVariable UUID invitationId) {
        Invitation invitation = invitationService.extendExpiryDate(invitationId);
        return new ResponseEntity<>(invitationMapper.toInvitationDTO(invitation), HttpStatus.OK);
    }

    /*
    @PostMapping
    public ResponseEntity<Invitation> createInvitation(@RequestBody Invitation invitation) {
        Invitation createdInvitation = invitationService.createInvitation(invitation);
        return new ResponseEntity<>(createdInvitation, HttpStatus.CREATED);
    }

    @GetMapping("/{invitationId}")
    public ResponseEntity<Invitation> getInvitationById(@PathVariable UUID invitationId) {
        Invitation invitation = invitationService.findInvitationById(invitationId);
        return new ResponseEntity<>(invitation, HttpStatus.OK);
    }

    @PutMapping("/{invitationId}")
    public ResponseEntity<Invitation> updateInvitation(@RequestBody Invitation updatedInvitation, @PathVariable UUID invitationId) {
        Invitation invitation = invitationService.updateInvitation(updatedInvitation, invitationId);
        return new ResponseEntity<>(invitation, HttpStatus.OK);
    }

    @DeleteMapping("/{invitationId}")
    public ResponseEntity<Void> deleteInvitation(@PathVariable UUID invitationId) {
        invitationService.deleteInvitationById(invitationId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/accept/{invitationId}")
    @Operation(summary = "Accept the invitation")
    public ResponseEntity<Invitation> acceptInvitation(@PathVariable UUID invitationId) {
        Invitation invitation = invitationService.acceptInvitation(invitationId);
        return new ResponseEntity<>(invitation, HttpStatus.OK);
    }

    @PostMapping("/decline/{invitationId}")
    @Operation(summary = "decline the invitation")
    public ResponseEntity<Invitation> declineInvitation(@PathVariable UUID invitationId) {
        Invitation invitation = invitationService.declineInvitation(invitationId);
        return new ResponseEntity<>(invitation, HttpStatus.OK);
    }

    @PostMapping("/extend/{invitationId}")
    @Operation(summary = "Extend the time of invitation by 30 days")
    public ResponseEntity<Invitation> extendExpiryDate(@PathVariable UUID invitationId) {
        Invitation invitation = invitationService.extendExpiryDate(invitationId);
        return new ResponseEntity<>(invitation, HttpStatus.OK);
    }

     */
}
