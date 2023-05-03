package com.santa.work.DTOTests;

import com.github.javafaker.Faker;
import com.santa.work.dto.MatchDTO;
import com.santa.work.entity.Match;
import com.santa.work.entity.SecretSantaGroup;
import com.santa.work.entity.Users;
import com.santa.work.enumeration.Role;
import com.santa.work.mapper.MatchMapper;
import com.santa.work.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.UUID;

@SpringBootTest
public class DTOIntegrationTestsMatch {

    @Autowired
    private final UserRepository userRepository;
    private final MatchMapper matchMapper;

    Faker faker = new Faker();
    @Autowired
    public DTOIntegrationTestsMatch(UserRepository userRepository, MatchMapper matchMapper) {
        this.userRepository = userRepository;
        this.matchMapper = matchMapper;
    }

    @Test
    public void testMappingBetweenMatchAndMatchDTO() {
        // Prepare data
        UUID matchId = UUID.randomUUID();
        Users giverUser = new Users();
        Users receiverUser = new Users();
        SecretSantaGroup group = createSecretSantaGroupEntity();

        // Create Match entity
        Match match = new Match();
        match.setId(matchId);
        match.setGiverUser(giverUser);
        match.setReceiverUser(receiverUser);
        match.setRevealed(false);
        match.setGroup(group);

        // Map Match entity to MatchDTO
        MatchDTO matchDTO = matchMapper.toMatchDTO(match);

        // Console log and logger statements
        System.out.println("MatchDTO: " + matchDTO);

        // Verify mapping
        assertEquals(matchId, matchDTO.getId());
        //assertEquals(giverUser.getId(), matchDTO.getGiverUserId());
        assertEquals(receiverUser.getId(), matchDTO.getReceiverUserId());
        //assertEquals(false, matchDTO.isRevealed());
        //assertEquals(group.getId(), matchDTO.getGroupId());

        // Map MatchDTO back to Match entity
        Match mappedMatch = MatchMapper.toMatchEntity(matchDTO, giverUser, receiverUser, group);

        // Console log and logger statements
        System.out.println("Mapped Match: " + mappedMatch);

        // Verify mapping
        assertEquals(matchId, mappedMatch.getId());
        assertEquals(giverUser.getId(), mappedMatch.getGiverUser().getId());
        assertEquals(receiverUser.getId(), mappedMatch.getReceiverUser().getId());
        assertEquals(false, mappedMatch.isRevealed());
        assertEquals(group.getId(), mappedMatch.getGroup().getId());
    }
        private Users createUser() {
            Users user = new Users();
            user.setFirstname(faker.name().firstName());
            user.setLastname(faker.name().lastName());
            user.setLogin(faker.name().username());
            user.setPassword(faker.internet().password());
            user.setRole(true ? Role.ADMIN : Role.USER);
            return userRepository.save(user);
        }


        private SecretSantaGroup createSecretSantaGroupEntity() {
            SecretSantaGroup group = new SecretSantaGroup();
            group.setName(faker.name().bloodGroup());
            group.setAdmin(createUser());
            group.setUrl(faker.internet().url());
            group.setMatchesGenerated(false);
            return group;
        }

}
