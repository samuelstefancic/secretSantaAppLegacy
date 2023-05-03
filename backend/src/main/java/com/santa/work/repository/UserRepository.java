package com.santa.work.repository;

import com.santa.work.dto.SecretSantaGroupDTO;
import com.santa.work.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<Users, UUID> {
    Users findByFirstnameAndLastnameAndId(String firstname, String lastname, UUID id);
    Optional<Users> findByLogin(String login);
    List<SecretSantaGroupDTO> findAllSecretSantaGroupById(UUID userId);

    Optional<Users> findByEmail(String email);
}
