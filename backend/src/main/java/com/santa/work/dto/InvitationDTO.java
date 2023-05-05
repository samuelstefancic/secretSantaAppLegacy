package com.santa.work.dto;

import com.santa.work.enumeration.InvitationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvitationDTO {
    private UUID id;
    private String invitationName;
    private String email;
    private String groupUrl;
    private String token;
    private InvitationStatus invitationStatus;
    private LocalDateTime expiryDate;
    private UUID groupId;
    private UUID invitedUserId;
    private UUID senderId;
}
