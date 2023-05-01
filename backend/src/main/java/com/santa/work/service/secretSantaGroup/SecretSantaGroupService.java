package com.santa.work.service.secretSantaGroup;

import com.santa.work.dto.SecretSantaGroupDTO;
import com.santa.work.dto.UserDTO;
import com.santa.work.entity.SecretSantaGroup;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
@Service
public interface SecretSantaGroupService {
    SecretSantaGroup createSecretSantaGroup(SecretSantaGroup secretSantaGroup, UUID creatorUserId);
    SecretSantaGroup findSecretSantaGroupById(UUID id);
    SecretSantaGroup updateSecretSantaGroup(UUID id, SecretSantaGroup secretSantaGroup);
    void deleteSecretSantaGroupById(UUID id);
    List<SecretSantaGroup> getAllSecretSantaGroups();

    //DTO

   // List<SecretSantaGroupDTO> findAllSecretSantaGroupsByUserId(UUID userId);
}
