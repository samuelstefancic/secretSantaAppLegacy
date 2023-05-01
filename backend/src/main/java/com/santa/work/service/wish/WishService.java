package com.santa.work.service.wish;

import com.santa.work.entity.Wish;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
@Service
public interface WishService {
    Wish createWish(Wish wish);
    Wish findWishById(UUID id);
    Wish findByTitle(String title);
    Wish findByDescription(String description);
    Wish findByTitleAndDescription(String title, String description);
    Wish findByDescriptionAndTitleAndId(String description, String title, UUID id);
    Wish updateWish(Wish wish, UUID id);
    void deleteWishById(UUID id);
    List<Wish> getAllWishes();
}
