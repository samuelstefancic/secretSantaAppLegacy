package com.santa.work.controller;

import com.santa.work.dto.MatchDTO;
import com.santa.work.entity.Match;
import com.santa.work.entity.Users;
import com.santa.work.mapper.MatchMapper;
import com.santa.work.service.match.MatchServiceImpl;
import com.santa.work.service.user.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/matches")
@Slf4j
public class MatchController {

    private final MatchServiceImpl matchService;
    private final UserServiceImpl userService;
    private final MatchMapper matchMapper;

    @Autowired
    public MatchController(@Lazy MatchServiceImpl matchService, UserServiceImpl userService, MatchMapper matchMapper) {
        this.matchService = matchService;
        this.userService = userService;
        this.matchMapper = matchMapper;
    }

    @Operation(summary = "Generate matches for the whole group")
    @PostMapping("/generate/{groupId}/{adminId}")
    public ResponseEntity<List<MatchDTO>> generateMatchesForGroup(@PathVariable UUID groupId, @PathVariable UUID adminId) {
        List<Match> generatedMatches = matchService.generateMatchesForGroup(groupId, adminId);
        List<MatchDTO> generatedMatchDtos = generatedMatches.stream().map(matchMapper::toMatchDTO).toList();
        return new ResponseEntity<>(generatedMatchDtos, HttpStatus.CREATED);
    }

    @Operation(summary = "Get a match by id")
    @GetMapping("/{id}")
    public ResponseEntity<MatchDTO> getMatchById(@PathVariable UUID id) {
        Match match = matchService.findMatchById(id);
        return new ResponseEntity<>(matchMapper.toMatchDTO(match), HttpStatus.OK);
    }

    @Operation(summary = "Get all matches for a user - giver")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<MatchDTO>> getAllMatchesForUser(@PathVariable UUID userId) {
        List<Match> matches = matchService.findMatchesByGiverUserId(userId);
        List<MatchDTO> matchDtos = matches.stream().map(matchMapper::toMatchDTO).toList();
        return new ResponseEntity<>(matchDtos, HttpStatus.OK);
    }

    @Operation(summary = "Get all matches from a group")
    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<MatchDTO>> getMatchesByGroupdId(@PathVariable UUID groupId) {
        List<Match> matches = matchService.findMatchesByGroupId(groupId);
        List<MatchDTO> matchDtos = matches.stream().map(matchMapper::toMatchDTO).toList();
        return new ResponseEntity<>(matchDtos, HttpStatus.OK);
    }

    @Operation(summary = "Get all matches")
    @GetMapping("/all")
    public ResponseEntity<List<MatchDTO>> getAllMatches() {
        List<Match> matches = matchService.getAllMatches();
        List<MatchDTO> matchDTOs = matchMapper.toMatchDtos(matches);
        return new ResponseEntity<>(matchDTOs, HttpStatus.OK);
    }

    @DeleteMapping("/{matchId}")
    public ResponseEntity<Void> deleteMatch(@PathVariable UUID matchId) {
        matchService.deleteMatchById(matchId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /*
    @Operation(summary = "Basic post method, create a match")
    @PostMapping
    public ResponseEntity<Match> createMatch(@Valid @RequestBody Match match) {
        Match createdMatch = matchService.createMatch(match);
        return new ResponseEntity<>(createdMatch, HttpStatus.CREATED);
    }



    @Operation(summary = "Get all matches")
    @GetMapping("/{id}")
    public ResponseEntity<Match> getMatchById2(@PathVariable UUID id) {
        Match match = matchService.findMatchById(id);
        return new ResponseEntity<>(match, HttpStatus.OK);
    }

    @PutMapping("/{matchId}")
    public ResponseEntity<Match> updateMatch(@RequestBody Match updatedMatch, @PathVariable UUID matchId) {
        Match match = matchService.updateMatch(updatedMatch, matchId);
        return new ResponseEntity<>(match, HttpStatus.OK);
    }

    @DeleteMapping("/{matchId}")
    public ResponseEntity<Void> deleteMatch(@PathVariable UUID matchId) {
        matchService.deleteMatchById(matchId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/generate/{groupId}")
    @Operation(summary = "Generate matches for group")
    public ResponseEntity<List<Match>> generateMatchesForGroup(@PathVariable UUID groupId, Principal principal) {
        Users currentUser = userService.findUserByLogin(principal.getName());
        List<Match> matches = matchService.generateMatchesForGroup(groupId, currentUser.getId());
        return new ResponseEntity<>(matches, HttpStatus.CREATED);
    }
*/
}
