package com.santa.work.mapper;

import com.santa.work.entity.Users;
import com.santa.work.service.user.UserServiceImpl;

import java.util.List;
import java.util.UUID;

public interface UserMapperDelegate {
    List<UUID> toUUIDs(List<Users> users);
    List<Users> toUsers(List<UUID> userIds, UserServiceImpl userService);
}
