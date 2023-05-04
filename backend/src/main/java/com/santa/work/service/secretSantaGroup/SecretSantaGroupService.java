package com.santa.work.service.secretSantaGroup;

import com.santa.work.dto.SecretSantaGroupDTO;
import com.santa.work.dto.UserDTO;
import com.santa.work.entity.SecretSantaGroup;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
@Service
public interface SecretSantaGroupService {

    //Create

    SecretSantaGroup createSecretSantaGroup(SecretSantaGroup secretSantaGroup, UUID creatorUserId);
    SecretSantaGroup addUserToGroup(UUID userId, UUID groupId);

    //Read

    SecretSantaGroup findSecretSantaGroupById(UUID santaGroupId);
    List<SecretSantaGroup> findAllSecretSantaGroups();
    List<SecretSantaGroupDTO> findSecretSantaGroupDTOsByIds(List<UUID> groupIds);
    List<SecretSantaGroup> findSecretSantaGroupEntitiesByIds(List<UUID> groupIds, UUID adminId);
    List<SecretSantaGroup> getAllSecretSantaGroupsByAdminId(UUID adminId);

    //Update

    SecretSantaGroup updateSecretSantaGroup(UUID id, SecretSantaGroupDTO updatedGroupDTO);

    //Delete

    void deleteSecretSantaGroupById(UUID id);
    List<SecretSantaGroup> getAllSecretSantaGroups();


    //Url handler

    String generateUniqueGroupUrl(String groupName, UUID groupId);


}
