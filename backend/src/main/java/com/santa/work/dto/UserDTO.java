package com.santa.work.dto;

import com.santa.work.enumeration.Role;
import lombok.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private UUID id;
    private Role role;
    private String firstname;
    private String lastname;
    private String login;
    private String password;
    private List<WishDTO> wishList;
    private List<UUID> groupIds;
    private Set<InvitationDTO> invitations;
}
