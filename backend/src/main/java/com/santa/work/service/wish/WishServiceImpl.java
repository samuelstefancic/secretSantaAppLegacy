package com.santa.work.service.wish;

import com.santa.work.entity.Users;
import com.santa.work.entity.Wish;
import com.santa.work.exception.usersExceptions.UserNotFoundException;
import com.santa.work.exception.wishExceptions.WishException;
import com.santa.work.repository.UserRepository;
import com.santa.work.repository.WishRepository;
import jakarta.transaction.Transactional;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class WishServiceImpl implements WishService {
    private final WishRepository wishRepository;
    private final UserRepository userRepository;
    @Autowired
    public WishServiceImpl(WishRepository wishRepository,@Lazy UserRepository userRepository) {this.wishRepository = wishRepository;
        this.userRepository = userRepository;
    }

    public Wish createWish(Wish wish, UUID userId) {
        try {
            Users user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("User with id " + userId + " not found"));
            wish.setUsers(user);
            Wish createdWish = wishRepository.save(wish);
            if (createdWish.getId() == null) {
                throw new WishException("Failed to create Wish, id is null", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            user.getWishList().add(createdWish);
            userRepository.save(user);
            return createdWish;
        } catch (DataAccessException | ConstraintViolationException e) {
            throw new WishException("Failed to create Wish " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    public Wish findWishById(UUID wishId) {
        return wishRepository.findById(wishId)
                .orElseThrow(() -> new WishException("Wish with id " + wishId + " not found", HttpStatus.NOT_FOUND));
    }

    public Wish findByTitle(String title) {
        return wishRepository.findByTitle(title)
                .orElseThrow(() -> new WishException("Wish with title " + title + " not found", HttpStatus.NOT_FOUND));
    }

    public List<Wish> findWishByTitles(String title) {
        List<Wish> wishes = wishRepository.findByTitleContainingIgnoreCase(title);
        if (wishes.isEmpty()) {
            return Collections.emptyList();
        }
        return wishes;
    }

    public List<Wish> findWishByDescriptions(String description) {
        List<Wish> descriptions = wishRepository.findByDescriptionContainingIgnoreCase(description);
        if (descriptions.isEmpty()) {
            return Collections.emptyList();
        }
        return descriptions;
    }

    public List<Wish> findWishByTitlesAndDescriptions(String title, String description) {
        List<Wish> wishes = wishRepository.findByTitleContainingIgnoreCaseAndDescriptionContainingIgnoreCase(title, description);
        if (wishes.isEmpty()) {
            return Collections.emptyList();
        }
        return wishes;
    }

    public Wish findByDescription(String description) {
        return wishRepository.findByDescription(description)
                .orElseThrow(() -> new WishException("Wish with description " + description + " not found", HttpStatus.NOT_FOUND));
    }

    public Wish findByTitleAndDescription(String title, String description) {
        return wishRepository.findByTitleAndDescription(title, description)
                .orElseThrow(() -> new WishException("Wish with title '"+ title + "' and description '" + description + "' not found", HttpStatus.NOT_FOUND));
    }

    public Wish findByDescriptionAndTitleAndId(String description, String title, UUID wishId) {
        if (!wishRepository.existsById(wishId)) {
            throw new WishException("Content with Id " + wishId + " not foud", HttpStatus.NOT_FOUND);
        }
        return wishRepository.findByDescriptionAndTitleAndId(description, title, wishId)
                .orElseThrow(() -> new WishException("Wish with id + " + wishId + " and with title '"+ title + "' and description '" + description + "' not found", HttpStatus.NOT_FOUND));
    }

    public Wish updateWish(Wish updatedWish, UUID id) {
        Wish wish = wishRepository.findById(id)
                .orElseThrow(() -> new WishException("The wish with id " + id + " does not exist", HttpStatus.NOT_FOUND));

        // Get the User object from the existing wish and set it to the updated wish
        Users user = wish.getUsers();
        updatedWish.setUsers(user);

        updatedWish.setUrl(updatedWish.getUrl());
        updatedWish.setDescription(updatedWish.getDescription());
        updatedWish.setTitle(updatedWish.getTitle());
        return wishRepository.save(updatedWish);
    }


    public void deleteWishById(UUID id) {
        if (!wishRepository.existsById(id)) {
            throw new WishException("Wish with ID : " + id + " not found", HttpStatus.NOT_FOUND);
        }
        try {
            wishRepository.deleteById(id);
        } catch (Exception e) {
            throw new WishException("Failed to delete Wish with id " + id + " ", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public List<Wish> getAllWishes() {
        List<Wish> wishes = wishRepository.findAll();
        if (wishes == null || wishes.isEmpty()) {
            return Collections.emptyList();
        }
        return wishes;
    }
}
