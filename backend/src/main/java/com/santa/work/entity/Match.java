package com.santa.work.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Table(name = "match")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "UUID")
    @SequenceGenerator(name = "UUID", sequenceName = "sequence_match", allocationSize = 1)
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name ="id_match", nullable = false, unique = true)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="giver_user_id")
    private Users giverUser;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_user_id")
    private Users receiverUser;

    private boolean isRevealed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private SecretSantaGroup group;
}
