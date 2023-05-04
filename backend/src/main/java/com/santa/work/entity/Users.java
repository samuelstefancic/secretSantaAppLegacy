package com.santa.work.entity;

import com.santa.work.enumeration.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "UUID")
    @SequenceGenerator(name="UUID", sequenceName = "sequence_user", allocationSize = 1)
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id_user", nullable = false, unique = true)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Column(name = "firstname", nullable = false)
    private String firstname;

    @Column(name="lastname", nullable = false)
    private String lastname;

    @Column(name="login", nullable = false, unique = true)
    private String login;

    @Column(name="password", nullable = false)
    private String password;

    @Column(name ="email", nullable = false, unique = true)
    private String email;

    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Wish> wishList = new ArrayList<>();

    @ManyToMany(mappedBy = "members", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<SecretSantaGroup> groups = new ArrayList<>();

    @OneToMany(mappedBy = "invitedUser")
    private Set<Invitation> invitations;

    public void addGroup(SecretSantaGroup group) {
        groups.add(group);
        group.addMember(this);
    }
}
