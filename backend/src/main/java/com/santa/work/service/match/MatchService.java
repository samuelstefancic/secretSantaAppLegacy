package com.santa.work.service.match;

import com.santa.work.entity.Match;
import com.santa.work.entity.Users;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
@Service
public interface MatchService {
    Match createMatch(Match match);
    Match findMatchById(UUID id);
    Match updateMatch(Match match, UUID id);
    void deleteMatchById(UUID id);
    List<Match> getAllMatches();
    List<Match> findMatchesByIds(List<UUID> matchIds);
    List<Match> findMatchesByGiverUserId(UUID giverUserId);

}
