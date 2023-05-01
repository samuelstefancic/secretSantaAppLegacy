package com.santa.work.service.wish;

import com.santa.work.entity.Wish;
import com.santa.work.exception.wishExceptions.WishException;
import com.santa.work.repository.WishRepository;
import jakarta.transaction.Transactional;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class WishServiceImpl implements WishService {
    private final WishRepository wishRepository;
    @Autowired
    public WishServiceImpl(WishRepository wishRepository) {this.wishRepository = wishRepository;}

    public Wish createWish(Wish wish) {
        try {
            Wish createdWish = wishRepository.save(wish);
            if (createdWish.getId() == null) {
                throw new WishException("Failed to create Wish, id is null", HttpStatus.INTERNAL_SERVER_ERROR);
            }
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
        wish.setUsers(updatedWish.getUsers());
        wish.setUrl(updatedWish.getUrl());
        wish.setDescription(updatedWish.getDescription());
        wish.setTitle(updatedWish.getTitle());
        return wishRepository.save(wish);
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
        return wishRepository.findAll();
    }
}
