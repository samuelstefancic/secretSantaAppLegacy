package com.santa.work.service.invitation;

import com.santa.work.dto.InvitationDTO;
import com.santa.work.entity.Invitation;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
@Service
public interface InvitationService {

    Invitation createInvitation(InvitationDTO invitationDTO, UUID groupId, UUID senderId);
    Invitation findInvitationById(UUID invitationId);
    Invitation updateInvitation(Invitation invitation, UUID id);
    void deleteInvitationById(UUID id);
    Set<Invitation> getAllInvitations();
    Invitation acceptInvitation(UUID invitationId);
    Invitation declineInvitation(UUID invitationId);
    Invitation extendExpiryDate(UUID invitationId);
    String generateUniqueToken();
    List<Invitation> findInvitationsByIds(List<UUID> invitationIds);
    List<Invitation> getAllInvitationsByIds(List<UUID> invitationsIds);
}
