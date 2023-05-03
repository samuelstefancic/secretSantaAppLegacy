package com.santa.work.repository;

import com.santa.work.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface MatchRepository extends JpaRepository<Match, UUID> {
    List<Match> findByGiverUser_Id(UUID giverUserId);
    List<Match> findByGroup_Id(UUID groupId);
}
