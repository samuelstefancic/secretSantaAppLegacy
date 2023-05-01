package com.santa.work.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatchDTO {
    private UUID id;
    private UUID giverUserId;
    private UUID receiverUserId;
    private boolean isRevealed;
    private UUID groupId;
}