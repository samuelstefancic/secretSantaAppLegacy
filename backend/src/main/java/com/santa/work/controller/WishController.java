package com.santa.work.controller;

import com.santa.work.entity.Wish;
import com.santa.work.service.wish.WishServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/wishes")
@Slf4j
public class WishController {
    private final WishServiceImpl wishService;
    @Autowired
    public WishController(WishServiceImpl wishService){this.wishService = wishService;}

    //Post
    @PostMapping
    public ResponseEntity<Wish> createWish(@Valid @RequestBody Wish wish) {
        Wish createdWish = wishService.createWish(wish);
        return new ResponseEntity<>(createdWish, HttpStatus.CREATED);
    }

    //Get
    @GetMapping
    public ResponseEntity<List<Wish>> getAllWishes() {
        List<Wish> wishes = wishService.getAllWishes();
        return new ResponseEntity<>(wishes, HttpStatus.OK);
    }

    @Operation(summary = "Get wish by ID")
    @GetMapping("/{id}")
    public ResponseEntity<Wish> getWishById(@PathVariable UUID id) {
        Wish wish = wishService.findWishById(id);
        return new ResponseEntity<>(wish, HttpStatus.OK);
    }

    //Update
    @PutMapping("/{id}")
    public ResponseEntity<Wish> updateWish(@PathVariable UUID id, @Valid @RequestBody Wish wish) {
        Wish updatedWish = wishService.updateWish(wish, id);
        return new ResponseEntity<>(updatedWish, HttpStatus.OK);
    }

    //Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Wish> deleteWish(@PathVariable UUID id) {
        wishService.deleteWishById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //Not Crud basic stuff
    //Maybe switch to list if there's multiple with the same
    @Operation(summary = "Get a wish with the title")
    @GetMapping("/title/{title}")
    public ResponseEntity<Wish> findByTitle(@PathVariable String title) {
        Wish wish = wishService.findByTitle(title);
        return new ResponseEntity<>(wish, HttpStatus.OK);
    }

    @Operation(summary = "Get a wish with his description")
    @GetMapping("/description/{description}")
    public ResponseEntity<Wish> findByDescription(@PathVariable String description) {
        Wish wish = wishService.findByDescription(description);
        return new ResponseEntity<>(wish, HttpStatus.OK);
    }

    @Operation(summary = "Find wish title && description")
    @GetMapping("/title/{title}/description/{description}")
    public ResponseEntity<Wish> findByTitleAndDescription(@PathVariable String title, @PathVariable String description) {
        Wish wish = wishService.findByTitleAndDescription(title, description);
        return new ResponseEntity<>(wish, HttpStatus.OK);
    }

    @Operation(summary = "Find wish description title ID")
    @GetMapping("/description/{description}/title/{title}/id/{wishId}")
    public ResponseEntity<Wish> findByDescriptionAndTitleAndId(@PathVariable String description, @PathVariable String title, @PathVariable UUID wishId) {
        Wish wish = wishService.findByDescriptionAndTitleAndId(description, title, wishId);
        return new ResponseEntity<>(wish, HttpStatus.OK);
    }

}
