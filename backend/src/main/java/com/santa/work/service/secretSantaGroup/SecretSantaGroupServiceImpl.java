package com.santa.work.service.secretSantaGroup;

import com.santa.work.dto.SecretSantaGroupDTO;
import com.santa.work.dto.UserDTO;
import com.santa.work.entity.Invitation;
import com.santa.work.entity.Match;
import com.santa.work.entity.SecretSantaGroup;
import com.santa.work.entity.Users;
import com.santa.work.enumeration.Role;
import com.santa.work.exception.secretSantaGroupExceptions.SecretSantaGroupException;
import com.santa.work.exception.usersExceptions.UsersException;
import com.santa.work.mapper.SecretSantaGroupMapper;
import com.santa.work.repository.SecretSantaGroupRepository;
import com.santa.work.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Transactional
public class SecretSantaGroupServiceImpl implements SecretSantaGroupService {

    private final SecretSantaGroupRepository secretRepository;

    private final UserRepository userRepository;

    private final SecretSantaGroupRepository secretSantaGroupRepository;

    private final SecretSantaGroupMapper secretSantaGroupMapper;

    @Autowired
    public SecretSantaGroupServiceImpl(@Lazy SecretSantaGroupRepository secretRepository, UserRepository userRepository, SecretSantaGroupRepository secretSantaGroupRepository,@Lazy SecretSantaGroupMapper secretSantaGroupMapper ) {
        this.secretRepository = secretRepository;
        this.userRepository = userRepository;
        this.secretSantaGroupRepository = secretSantaGroupRepository;
        this.secretSantaGroupMapper = secretSantaGroupMapper;
    }


    public SecretSantaGroup createSecretSantaGroup(SecretSantaGroup secretSantaGroup, UUID creatorId) {
        try {
            SecretSantaGroup createdGroup = secretSantaGroupRepository.save(secretSantaGroup);

            // Validate if the creator user has the appropriate role (admin)
            Users creator = userRepository.findById(creatorId)
                    .orElseThrow(() -> new SecretSantaGroupException("User not found", HttpStatus.NOT_FOUND));
            creator.setRole(Role.ADMIN);

            //Define the group creator as admin
            secretSantaGroup.setAdmin(creator);
            if (!creator.getRole().equals(Role.ADMIN)) {
                throw new SecretSantaGroupException("User does not have the appropriate role to create a group", HttpStatus.FORBIDDEN);
            }

            //Add the creator as a group member
            secretSantaGroup.getMembers().add(creator);
            //Add the groups in the List of groups of the creator
            creator.getGroups().add(secretSantaGroup);

            String uniqueUrl = generateUniqueGroupUrl(secretSantaGroup.getName(),secretSantaGroup.getId());
            secretSantaGroup.setUrl(uniqueUrl);

            if (createdGroup.getId() == null) {
                throw new SecretSantaGroupException("Failed to create Secret Santa Group: group id is null", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return createdGroup;
        } catch (DataAccessException | ConstraintViolationException e) {
            throw new SecretSantaGroupException("Failed to create Secret Santa Group: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public SecretSantaGroup findSecretSantaGroupById(UUID santaGroupId) {
        return secretRepository.findById(santaGroupId)
                .orElseThrow(() -> new UsersException("User with id " + santaGroupId + " not found", HttpStatus.NOT_FOUND));
    }

    public SecretSantaGroup updateSecretSantaGroup(UUID id, SecretSantaGroup updatedGroup) {
        SecretSantaGroup group = secretRepository.findById(id)
                .orElseThrow(() -> new SecretSantaGroupException("The secret santa group with ID : " + id + " not found", HttpStatus.NOT_FOUND));
        // Validate if the new admin exists
        Users newAdmin = userRepository.findById(updatedGroup.getAdmin().getId())
                .orElseThrow(() -> new SecretSantaGroupException("New admin user not found", HttpStatus.NOT_FOUND));
        group.setAdmin(newAdmin);
        // Update the simple entity
        group.setName(updatedGroup.getName());

        // Update the member list and validate new members
        group.getMembers().removeIf(member -> !updatedGroup.getMembers().contains(member));
        for (Users member : updatedGroup.getMembers()) {
            Users existingMember = userRepository.findById(member.getId())
                    .orElseThrow(() -> new SecretSantaGroupException("Member user not found", HttpStatus.NOT_FOUND));
            group.getMembers().add(existingMember);
        }

        // Update invitation List
        group.getInvitations().removeIf(invitation -> !updatedGroup.getInvitations().contains(invitation));
        for (Invitation updatedInvitation : updatedGroup.getInvitations()) {
            //Looking for the Id who is the same as the updated Id for the list invitation
            Optional<Invitation> existInviOpt = group.getInvitations().stream().filter(invitation -> invitation.getId().equals(updatedInvitation.getId())).findFirst();
            if (existInviOpt.isPresent()) {
                //Update invitation who exists
                Invitation existInvitation = existInviOpt.get();
                existInvitation.setEmail(updatedInvitation.getEmail());
                existInvitation.setToken(updatedInvitation.getToken());
                existInvitation.setExpiryDate(updatedInvitation.getExpiryDate());
            } else {
                group.addInvitation(updatedInvitation);
            }
        }
        // Update match list and validate new matches
        group.getMatches().removeIf(match -> !updatedGroup.getMatches().contains(match));
        for (Match updatedMatch : updatedGroup.getMatches()) {
            // Find the existing match for the same match ID
            Optional<Match> existMatchOpt = group.getMatches().stream().filter(match -> match.getId().equals(updatedMatch.getId())).findFirst();
            if (existMatchOpt.isPresent()) {
                Match matchExist = existMatchOpt.get();
                matchExist.setGiverUser(updatedMatch.getGiverUser());
                matchExist.setReceiverUser(updatedMatch.getReceiverUser());
                matchExist.setRevealed(updatedMatch.isRevealed());
            } else {
                group.addMatch(updatedMatch);
            }
        }
        return secretRepository.save(group);
    }

    public void deleteSecretSantaGroupById(UUID id) {
        if (!secretRepository.existsById(id)) {
            throw new SecretSantaGroupException("Secret Santa Group with ID : " + id + " not found", HttpStatus.NOT_FOUND);
        }
        try {
            secretRepository.deleteById(id);
        } catch (Exception e) {
            throw new SecretSantaGroupException("Failed to delete Secret Santa Group with ID : " + id, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    public List<SecretSantaGroup> getAllSecretSantaGroups() {
        return secretRepository.findAll();
    }

    public List<SecretSantaGroup> getAllSecretSantaGroupsByAdminId(UUID adminId) {
        return secretRepository.findAllByAdminId(adminId);
    }

    /*
    Méthode à supprimer ou a remplacer dans le futur
    public List<SecretSantaGroupDTO> findAllSecretSantaGroupsByUserId(UUID admin_id) {
        List<SecretSantaGroup> secretSantaGroups = secretRepository.findAllByAdmin_Id(admin_id);
        List<SecretSantaGroupDTO> secretSantaGroupDTOs = secretSantaGroupMapper.toSecretSantaGroupDTOs(secretSantaGroups);
        return secretSantaGroupDTOs;
    }
*/

    /**
     *
     * @param groupName
     * @param groupId
     * @return a unique groupUrl
     */
    public String generateUniqueGroupUrl(String groupName, UUID groupId) {
        String encryptedGroupName = encryptGroupName(groupName.toLowerCase().replaceAll("[^a-z0-9]+", ""));
        String uniqueIdentifier = UUID.randomUUID().toString().replace("-", "");
        String mixUrl;
        String shortenedUrl;
        do {
            mixUrl = mixUrlParts(encryptedGroupName, uniqueIdentifier, groupId.toString());
            shortenedUrl = mixUrl.substring(0,9).replace("-", "");
            uniqueIdentifier = UUID.randomUUID().toString().replace("-", "");
        } while (!isUrlValid(mixUrl) || urlExistsInDatabase(mixUrl));
        return shortenedUrl;
    }
    /**
     *
     * @param groupName
     * @return an encrypted group name
     */
    private String encryptGroupName(String groupName) {
        Random random = new Random();
        StringBuilder encryptedGroupName = new StringBuilder();
        for (char c : groupName.toCharArray()) {
            int newChar = random.nextInt(62);
            if (newChar < 10) {
                encryptedGroupName.append(newChar);
            } else if (newChar < 36) {
                encryptedGroupName.append((char) ('a' + (newChar - 10)));
            } else {
                encryptedGroupName.append((char) ('A' + (newChar - 36)));
            }
        }
        return encryptedGroupName.toString();
    }
    /**
     *
     * @param encryptedGroupName
     * @param uniqueIdentifier
     * @param groupId
     * @return a shuffled mixed element string
     */
    private String mixUrlParts(String encryptedGroupName, String uniqueIdentifier, String groupId) {
        //Shuffle text in the 3 different elements (lmfao)
        encryptedGroupName = shuffleText(encryptedGroupName);
        uniqueIdentifier = shuffleText(uniqueIdentifier);
        groupId = shuffleText(groupId);

        //Assemblate the parts random after the shuffle (everyday)
        List<String> parts = Arrays.asList(encryptedGroupName, uniqueIdentifier, groupId);
        Collections.shuffle(parts);
        return String.join("", parts);
    }
    /**
     *
     * @param text
     * @return a shuffled text
     */
    private String shuffleText(String text) {
        List<String> parts = new ArrayList<>(Arrays.asList(text.split("")));
        Collections.shuffle(parts);
        return String.join("", parts);
    }
    /**
     *
     * @param url
     * @return a verification to see if the URL have all the parameters
     */
    private boolean isUrlValid(String url) {
        boolean hasUpperCase = !url.equals(url.toLowerCase());
        boolean hasLowerCase = !url.equals(url.toUpperCase());
        boolean hasDigit = url.chars().anyMatch(Character::isDigit);
        return hasUpperCase && hasLowerCase && hasDigit;
    }

    /**
     *
     * @param url
     * @return a boolean to see if the url is present or not
     */
    private boolean urlExistsInDatabase(String url) {
        return secretRepository.findByUrl(url).isPresent();
    }

    //DTO
    public List<SecretSantaGroupDTO> findSecretSantaGroupDTOsByIds(List<UUID> groupIds) {
        List<SecretSantaGroup> secretSantaGroups = secretSantaGroupRepository.findAllByIdIn(groupIds);
        return secretSantaGroupMapper.toSecretSantaGroupDTOs(secretSantaGroups);
    }

    public List<SecretSantaGroup> findSecretSantaGroupEntitiesByIds(List<UUID> groupIds, UUID adminId) {
        return secretSantaGroupMapper.toSecretSantaGroupEntities(groupIds, adminId);
    }
/*
 //Déplacer cette méthode dans UserService
    public List<SecretSantaGroupDTO> findSecretSantaGroupDTOsByUserId(UUID userId) {
        List<SecretSantaGroup> secretSantaGroups = secretSantaGroupRepository.findAllSecretSantaGroupsByAdminId(userId);
        return secretSantaGroupMapper.toSecretSantaGroupDTOs(secretSantaGroups);
    }
*/

    public SecretSantaGroupDTO createSecretSantaGroupDTO(SecretSantaGroupDTO secretSantaGroupDTO, UUID creatorId) {
        SecretSantaGroup secretSantaGroup = secretSantaGroupMapper.toSecretSantaGroupEntity(secretSantaGroupDTO, creatorId);
        SecretSantaGroup createdGroup = createSecretSantaGroup(secretSantaGroup, creatorId);
        return secretSantaGroupMapper.toSecretSantaGroupDTO(createdGroup);
    }
}