package com.santa.work.entity;

import com.santa.work.enumeration.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"groups", "invitations"})
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
    private List<SecretSantaGroup> groups;

    @OneToMany(mappedBy = "invitedUser", fetch = FetchType.LAZY)
    private Set<Invitation> invitations = new HashSet<>();

    public List<SecretSantaGroup> getGroups() {
        if (groups == null) {
            groups = new ArrayList<>();
        }
        return groups;
    }

}
