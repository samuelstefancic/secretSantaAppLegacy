package com.santa.work.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WishDTO {
    private UUID id;
    private String title;
    private String description;
    private String url;
    private UUID userId;
}
