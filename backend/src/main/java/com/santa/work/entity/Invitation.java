package com.santa.work.entity;

import com.santa.work.enumeration.InvitationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "invitations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Invitation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "UUID")
    @SequenceGenerator(name = "UUID", sequenceName = "sequence_invitation", allocationSize = 1)
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id_invitation", nullable = false, unique = true)
    private UUID id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "token", nullable = false)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private InvitationStatus status;

    @Column(name="expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private SecretSantaGroup group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invited_user_id")
    private Users invitedUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_invitation_user_id")
    private Users sender;
}
