package com.santa.work.dto;

import com.santa.work.enumeration.Role;
import lombok.*;

import java.util.*;

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
    private String email;
    private List<WishDTO> wishList = new ArrayList<>();
    private List<UUID> groupIds = new ArrayList<>();
    private Set<InvitationDTO> invitations = new HashSet<>();
}
