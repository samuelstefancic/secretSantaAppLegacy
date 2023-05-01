package com.santa.work.entity;

import com.santa.work.enumeration.InvitationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "santa_group")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SecretSantaGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator="UUID")
    @SequenceGenerator(name="UUID", sequenceName = "sequence_santa")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name ="id_santa_group", nullable = false, unique = true)
    private UUID id;

    @Column(name = "secret_santa_group_name", nullable = false)
    private String name;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private Users admin;

    @Column(name = "group_url", unique = true)
    private String url;

    @Column(name = "matches_generated", nullable = false)
    private boolean matchesGenerated = false;

    //List des membres du groupe
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "group_users",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<Users> members = new ArrayList<>();

    //List invitation groupe secretsanta, invitation liée à un groupe
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Invitation> invitations = new ArrayList<>();

    //Liste de correspondance pour membre de chaque groupe, match associé à un groupe santa spécifique
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Match> matches = new ArrayList<>();

    public void addInvitation(Invitation invitation) {
        invitations.add(invitation);
        invitation.setGroup(this);
    }
    public void addMatch(Match match) {
        matches.add(match);
        match.setGroup(this);
    }
    public boolean allInvitationsAccepted() {
        for (Invitation invitation : this.invitations) {
            if (invitation.getStatus() != InvitationStatus.ACCEPTED) {
                return false;
            }
        }
        return true;
    }

}
