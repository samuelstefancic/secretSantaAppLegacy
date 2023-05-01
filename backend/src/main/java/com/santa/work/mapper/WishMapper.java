package com.santa.work.mapper;

import com.santa.work.dto.WishDTO;
import com.santa.work.entity.Users;
import com.santa.work.entity.Wish;
import com.santa.work.exception.wishExceptions.InvalidWishException;
import com.santa.work.exception.wishExceptions.WishNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
@Component
public class WishMapper {
    public static WishDTO toWishDTO(Wish wish) {
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
    public static Wish toWishEntity(WishDTO wishDTO, Users user) {
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
        wish.setUsers(user);
        return wish;
    }
    public static List<WishDTO> toWishDtos(List<Wish> wishes) {
        return wishes.stream().map(WishMapper::toWishDTO).collect(Collectors.toList());
    }
    public static List<Wish> toWishEntities(List<WishDTO> wishDtos, Users user) {
        return wishDtos.stream().map(wishDto -> toWishEntity(wishDto, user)).collect(Collectors.toList());
    }
}
