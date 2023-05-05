package com.santa.work.repository;

import com.santa.work.entity.Wish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface WishRepository extends JpaRepository<Wish, UUID> {
    List<Wish> findByUsers_Id(UUID userId);
    @Query("SELECT w from Wish w WHERE w.description = :description AND w.title = :title AND w.id = :wishId")
    Optional<Wish> findByDescriptionAndTitleAndId(@Param("description") String description,@Param("title") String title,@Param("wishId") UUID wishId);
    Optional<Wish> findByTitleAndDescription(String title, String description);
    Optional<Wish> findByTitle(String title);
    Optional<Wish> findByDescription(String description);
    List<Wish> findByTitleContainingIgnoreCase(String title);
    List<Wish> findByDescriptionContainingIgnoreCase(String description);
    List<Wish> findByTitleContainingIgnoreCaseAndDescriptionContainingIgnoreCase(String title, String description);
}
