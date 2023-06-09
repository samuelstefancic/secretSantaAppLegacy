package com.santa.work.entity;

import com.santa.work.enumeration.InvitationStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "invitations")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"group", "invitedUser", "sender"})
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

    @Column(name = "invitation_name", nullable = true)
    private String invitationName;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "group_url", nullable = true)
    private String groupUrl;

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
