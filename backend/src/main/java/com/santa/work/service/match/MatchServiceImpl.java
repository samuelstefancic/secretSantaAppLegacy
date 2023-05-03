package com.santa.work.service.match;

import com.santa.work.entity.Match;
import com.santa.work.entity.SecretSantaGroup;
import com.santa.work.entity.Users;
import com.santa.work.exception.matchExceptions.MatchException;
import com.santa.work.exception.secretSantaGroupExceptions.SecretSantaGroupException;
import com.santa.work.exception.usersExceptions.UsersException;
import com.santa.work.repository.InvitationRepository;
import com.santa.work.repository.MatchRepository;
import com.santa.work.repository.SecretSantaGroupRepository;
import com.santa.work.repository.UserRepository;
import com.santa.work.service.secretSantaGroup.SecretSantaGroupService;
import jakarta.transaction.Transactional;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class MatchServiceImpl implements MatchService{

    private final MatchRepository matchRepository;
    private final SecretSantaGroupService secretSantaGroupService;
    private final SecretSantaGroupRepository secretSantaGroupRepository;
    private final UserRepository userRepository;
    private final InvitationRepository invitationRepository;

    @Autowired
    public MatchServiceImpl(@Lazy MatchRepository matchRepository, SecretSantaGroupService secretSantaGroupService, SecretSantaGroupRepository secretSantaGroupRepository, UserRepository userRepository, InvitationRepository invitationRepository) {
        this.matchRepository = matchRepository;
        this.secretSantaGroupService = secretSantaGroupService;
        this.secretSantaGroupRepository = secretSantaGroupRepository;
        this.userRepository = userRepository;
        this.invitationRepository = invitationRepository;
    }

    public Match createMatch(Match match) {
        UUID giverUserId = match.getGiverUser().getId();
        UUID receiverUserId = match.getReceiverUser().getId();
        UUID groupId = match.getGroup().getId();
        // Check if giver, receiver, and group exist
        if (!userRepository.existsById(giverUserId) || !userRepository.existsById(receiverUserId) || !matchRepository.existsById(groupId)) {
            throw new MatchException("Giver, receiver, or group not found", HttpStatus.NOT_FOUND);
        }
        try {
            Match createdMatch = matchRepository.save(match);
            if (createdMatch.getId() == null) {
                throw new MatchException("Failed to create Match, id is null", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return createdMatch;
        } catch (DataAccessException | ConstraintViolationException e) {
            throw new MatchException("Failed to create Match " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Match findMatchById(UUID id) {
        return matchRepository.findById(id)
                .orElseThrow(() -> new MatchException("Match with ID : " + id + " not found", HttpStatus.NOT_FOUND));
    }

    public List<Match> findMatchesByIds(List<UUID> matchIds) {
        if (matchIds == null || matchIds.isEmpty()) {
            return Collections.emptyList();
        }
        return matchRepository.findAllById(matchIds);
    }

    public List<Match> findMatchesByGiverUserId(UUID giverUserId) {
        if (!userRepository.existsById(giverUserId)) {
            throw new MatchException("Giver with ID : " + giverUserId + " not found", HttpStatus.NOT_FOUND);
        }
        List<Match> matches = matchRepository.findByGiverUser_Id(giverUserId);

        if (matches == null || matches.isEmpty()) {
            return Collections.emptyList();
        }
        return matchRepository.findByGiverUser_Id(giverUserId);
    }

    public List<Match> findMatchesByGroupId(UUID groupId) {
        if (!secretSantaGroupRepository.existsById(groupId)) {
            throw new SecretSantaGroupException("Group with ID : " + groupId + " not found", HttpStatus.NOT_FOUND);
        }
        List<Match> matches = matchRepository.findByGroup_Id(groupId);
        if (matches == null || matches.isEmpty()) {
            return Collections.emptyList();
        }
        return matches;
    }

    public Match updateMatch(Match updatedMatch, UUID id) {
        Match match = matchRepository.findById(id)
                .orElseThrow(() -> new MatchException("The match with id " + id + " does not exist", HttpStatus.NOT_FOUND));

        UUID newGiverUserId = updatedMatch.getGiverUser().getId();
        UUID newReceiverUserId = updatedMatch.getReceiverUser().getId();
        UUID newGroupId = updatedMatch.getGroup().getId();
        if (!userRepository.existsById(newGiverUserId) || !userRepository.existsById(newReceiverUserId) || !invitationRepository.existsById(newGroupId)) {
            throw new MatchException("New giver, receiver, or group not found", HttpStatus.NOT_FOUND);
        }
        match.setGiverUser(updatedMatch.getGiverUser());
        match.setReceiverUser(updatedMatch.getReceiverUser());
        match.setRevealed(updatedMatch.isRevealed());
        match.setGroup(updatedMatch.getGroup());

        return matchRepository.save(match);
    }


    public void deleteMatchById(UUID id) {
        if (!matchRepository.existsById(id)) {
            throw new MatchException("Match With ID " + id + " not found", HttpStatus.NOT_FOUND);
        }
        try {
            matchRepository.deleteById(id);
        } catch (Exception e) {
            throw new MatchException("Failed to delete match with id : " + id + " ", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public List<Match> getAllMatches() {
        return matchRepository.findAll();
    }

    public List<Match> getAllMatchesByIds(List<UUID> matchesIds) {return matchRepository.findAllById(matchesIds);}

    public List<Match> generateMatchesForGroup(UUID groupId, UUID userId) {
        SecretSantaGroup group = secretSantaGroupRepository.findById(groupId).orElseThrow(
                () -> new SecretSantaGroupException("Secret Santa group not found.", HttpStatus.NOT_FOUND));
        Users user = userRepository.findById(userId).orElseThrow(
                () -> new UsersException("User not found.", HttpStatus.NOT_FOUND));
        if (group.isMatchesGenerated()) {
            throw new MatchException("Matches have already been generated for this group.", HttpStatus.BAD_REQUEST);
        }
        if (!group.getAdmin().getId().equals(user.getId())) {
            throw new MatchException("Only the group admin can generate matches.", HttpStatus.FORBIDDEN);
        }
        if (!group.allInvitationsAccepted()) {
            throw new MatchException("Cannot generate matches: not all invitations have been accepted. ", HttpStatus.BAD_REQUEST);
        }
        if (group.getInvitations().stream().anyMatch(invitation -> invitation.getExpiryDate().isBefore(LocalDateTime.now()))) {
            throw new MatchException("Cannot generate matches: some invitations have expired.", HttpStatus.BAD_REQUEST);
        }
        List<Users> members = group.getMembers();
        fisherYatesShuffle(members);

        boolean validShuffle = false;
        int shuffleAttempts = 0;
        int maxShuffleAttempts = 100; //Base value

        while (!validShuffle && shuffleAttempts < maxShuffleAttempts) {
            fisherYatesShuffle(members);
            validShuffle = true;
            for (int i = 0; i < members.size(); i++) {
                if (members.get(i).getId().equals(members.get((i + 1) % members.size()).getId())) {
                    validShuffle = false;
                    break;
                }
            }
            shuffleAttempts++;
        }

        if (!validShuffle) {
            // Handle the case when the algorithm couldn't find a valid shuffle after maxShuffleAttempts
            throw new MatchException("Failed to generate matches after " + maxShuffleAttempts + " attempts.", HttpStatus.INTERNAL_SERVER_ERROR);
        }


        List<Match> matches = new ArrayList<>();
        for (int i = 0; i < members.size(); i++) {
            Users giver = members.get(i);
            Users receiver = members.get((i + 1) % members.size());

            Match match = new Match();
            match.setGiverUser(giver);
            match.setReceiverUser(receiver);
            match.setGroup(group);

            match = matchRepository.save(match);
            matches.add(match);
        }
        group.setMatchesGenerated(true);
        secretSantaGroupRepository.save(group);
        return matches;
    }

    private void fisherYatesShuffle(List<Users> list) {
        Random random = new Random();
        for (int i = list.size() - 1; i > 0; i--) {
            int index = random.nextInt(i);
            Users temp = list.get(index);
            list.set(index, list.get(i));
            list.set(i, temp);
        }
    }
}
