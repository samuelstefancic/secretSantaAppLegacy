package com.santa.work.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SecretSantaGroupDTO {
    private UUID id;
    private String name;
    private UUID adminId;
    private String url;
    private boolean matchesGenerated;
    private List<UUID> memberIds = new ArrayList<>();
    private List<UUID> invitationIds = new ArrayList<>();
    private List<UUID> matchIds = new ArrayList<>();
}
