package com.santa.work.mapper;

import com.santa.work.dto.SecretSantaGroupDTO;
import com.santa.work.entity.Invitation;
import com.santa.work.entity.Match;
import com.santa.work.entity.SecretSantaGroup;
import com.santa.work.entity.Users;
import com.santa.work.service.invitation.InvitationServiceImpl;
import com.santa.work.service.match.MatchServiceImpl;
import com.santa.work.service.secretSantaGroup.SecretSantaGroupServiceImpl;
import com.santa.work.service.user.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;
@Component
public class SecretSantaGroupMapper {

    @Autowired
    private final UserMapperDelegate userMapper;
    @Autowired
    private final UserServiceImpl userService;
    @Autowired
    private final InvitationServiceImpl invitationService;
    @Autowired
    private final MatchServiceImpl matchService;
    @Autowired
    private final SecretSantaGroupServiceImpl secretService;
@Autowired
    public SecretSantaGroupMapper(@Lazy UserMapperDelegate userMapper, UserServiceImpl userService, InvitationServiceImpl invitationService, MatchServiceImpl matchService,@Lazy SecretSantaGroupServiceImpl secretService) {
        this.userMapper = userMapper;
        this.userService = userService;
        this.invitationService = invitationService;
        this.matchService = matchService;
        this.secretService = secretService;
    }

    public SecretSantaGroupDTO toSecretSantaGroupDTO(SecretSantaGroup group) {
        if (group == null) {
            return null;
        }
        UUID id = group.getId();
        String name = group.getName();
        UUID adminId = group.getAdmin().getId();
        String url = group.getUrl();
        boolean matchesGenerated = group.isMatchesGenerated();
        List<UUID> memberIds = userMapper.toUUIDs(group.getMembers());
        List<UUID> invitationIds = InvitationMapper.toUUIDs(group.getInvitations());
        List<UUID> matchIds = MatchMapper.toUUIDs(group.getMatches());

        return new SecretSantaGroupDTO(id, name, adminId, url, matchesGenerated, memberIds, invitationIds, matchIds);
    }
    public SecretSantaGroup toSecretSantaGroupEntity(SecretSantaGroupDTO groupDTO, UUID adminId) {
        if (groupDTO == null) {
            return null;
        }
        UUID id = groupDTO.getId();
        String name = groupDTO.getName();
        String url = groupDTO.getUrl();
        boolean matchesGenerated = groupDTO.isMatchesGenerated();
        Users admin = userService.findUserById(adminId);
        List<Users> members = userService.findUsersById(groupDTO.getMemberIds());
        List<Invitation> invitations = invitationService.findInvitationsByIds(groupDTO.getInvitationIds());
        List<Match> matches = matchService.findMatchesByIds(groupDTO.getMatchIds());
        return new SecretSantaGroup(id, name, admin, url, matchesGenerated, members, invitations, matches);
    }

    public List<SecretSantaGroup> toSecretSantaGroupEntities(List<UUID> groupIds, UUID adminId) {
        if (groupIds == null || groupIds.isEmpty()) {
            return Collections.emptyList();
        }
        List<SecretSantaGroupDTO> groupDTOs = secretService.findSecretSantaGroupDTOsByIds(groupIds);
        List<SecretSantaGroup> groups = new ArrayList<>();
        for (SecretSantaGroupDTO groupDTO : groupDTOs) {
            SecretSantaGroup group = toSecretSantaGroupEntity(groupDTO, adminId);
            groups.add(group);
        }
        return groups;
    }

    public List<UUID> toUUIDs(List<SecretSantaGroup> groups) {
        if (groups == null) {
            return Collections.emptyList();
        }
        return groups.stream().map(SecretSantaGroup::getId).collect(Collectors.toList());
    }
    public List<SecretSantaGroupDTO> toSecretSantaGroupDTOs(List<SecretSantaGroup> groups) {
        if (groups == null) {
            return Collections.emptyList();
        }
        return groups.stream().map(this::toSecretSantaGroupDTO).collect(Collectors.toList());
    }
}
