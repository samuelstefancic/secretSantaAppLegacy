package com.santa.work.mapper;

import com.santa.work.dto.MatchDTO;
import com.santa.work.entity.Match;
import com.santa.work.entity.SecretSantaGroup;
import com.santa.work.entity.Users;
import com.santa.work.exception.matchExceptions.InvalidMatchException;
import com.santa.work.exception.matchExceptions.MatchNotFoundException;
import com.santa.work.service.match.MatchServiceImpl;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
@Component
public class MatchMapper {
    public MatchDTO toMatchDTO(Match match) {
        if (match == null) {
            throw new MatchNotFoundException("Match not found");
        }
        return new MatchDTO(match.getId(), match.getReceiverUser().getId(), match.getReceiverUser().getFirstname(),
                match.getReceiverUser().getLastname());
    }
    public static Match toMatchEntity(MatchDTO matchDTO, Users giverUser, Users receiverUser, SecretSantaGroup group) {
        if (matchDTO == null) {
            throw new InvalidMatchException("Match is null");
        }
        Match match = new Match();
        match.setId(matchDTO.getId());
        match.setGiverUser(giverUser);
        match.setReceiverUser(receiverUser);
        match.setGroup(group);

        return match;
    }
    public static List<UUID> toUUIDs(List<Match> matches) {
        if (matches == null) {
            return Collections.emptyList();
        }
        return matches.stream().map(Match::getId).collect(Collectors.toList());
    }

    public List<MatchDTO> toMatchDtos(List<Match> matches) {
        if (matches == null) {
            return Collections.emptyList();
        }
        return matches.stream().map(this::toMatchDTO).collect(Collectors.toList());
    }

    public static List<Match> toMatches(List<UUID> matchesIds, MatchServiceImpl matchService) {
        if (matchesIds == null || matchesIds.isEmpty()) {
            return Collections.emptyList();
        }
        //Fetch matches from database with matchesIds
        return matchService.getAllMatchesByIds(matchesIds);
    }
}
