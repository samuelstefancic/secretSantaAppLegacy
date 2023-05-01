package com.santa.work.repository;

import com.santa.work.entity.Invitation;
import com.santa.work.entity.SecretSantaGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface InvitationRepository extends JpaRepository<Invitation, UUID> {

    List<Invitation> findByGroup(SecretSantaGroup group);
    boolean existsByToken(String token);
}
