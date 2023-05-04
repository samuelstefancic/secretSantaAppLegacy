package com.santa.work.mapper;

import com.santa.work.dto.WishDTO;
import com.santa.work.entity.Users;
import com.santa.work.entity.Wish;
import com.santa.work.exception.usersExceptions.UserNotFoundException;
import com.santa.work.exception.wishExceptions.InvalidWishException;
import com.santa.work.exception.wishExceptions.WishNotFoundException;
import com.santa.work.repository.UserRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
@Component
public class WishMapper {

    private final UserRepository userRepository;

    public WishMapper(@Lazy UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public WishDTO toWishDTO(Wish wish) {
            if (wish == null) {
                throw new WishNotFoundException("Wish not found");
            }

            UUID id = wish.getId();
            String title = wish.getTitle();
            String description = wish.getDescription();
            String url = wish.getUrl();
            UUID userId = wish.getUsers().getId();
            return new WishDTO(id, title, description, url, userId);
    }
    public Wish toWishEntity(WishDTO wishDTO) {
        if (wishDTO == null) {
            throw new InvalidWishException("Wish is null");
        }
        UUID id = wishDTO.getId();
        String title = wishDTO.getTitle();
        String description = wishDTO.getDescription();
        String url = wishDTO.getUrl();

        Wish wish = new Wish();
        wish.setId(id);
        wish.setTitle(title);
        wish.setDescription(description);
        wish.setUrl(url);

        return wish;
    }


    public List<WishDTO> toWishDtos(List<Wish> wishes) {
        return wishes.stream().map(this::toWishDTO).collect(Collectors.toList());
    }
    public List<Wish> toWishEntities(List<WishDTO> wishDtos) {
        return wishDtos.stream().map(this::toWishEntity).collect(Collectors.toList());
    }

}
