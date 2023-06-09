package com.santa.work.repository;

import com.santa.work.dto.SecretSantaGroupDTO;
import com.santa.work.entity.SecretSantaGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface SecretSantaGroupRepository extends JpaRepository<SecretSantaGroup, UUID> {

    Optional<SecretSantaGroup> findByUrl(String url);
    List<SecretSantaGroup> findAllByIdIn(List<UUID> groupIds);
    //List<SecretSantaGroup> findAllSecretSantaGroupsByAdminId(UUID userId);
    List<SecretSantaGroup> findAllByAdminId(UUID adminId);
   @Query("SELECT g FROM SecretSantaGroup g JOIN g.members m WHERE m.id = :admin_id")
   List<SecretSantaGroup> findAllByAdmin_Id(@Param("admin_id") UUID adminId);

}
