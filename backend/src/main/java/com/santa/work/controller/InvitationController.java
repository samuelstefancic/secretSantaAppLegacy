package com.santa.work.controller;

import com.santa.work.entity.Invitation;
import com.santa.work.service.invitation.InvitationServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/invitations")
@Slf4j
public class InvitationController {
    private final InvitationServiceImpl invitationService;

    @Autowired
    public InvitationController(InvitationServiceImpl invitationService){this.invitationService = invitationService;}

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
}
