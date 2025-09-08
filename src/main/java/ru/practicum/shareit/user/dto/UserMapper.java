package ru.practicum.shareit.user.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserMapper {

    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public static User toUser(UserDto itemDto) {
        return new User(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getEmail()
        );
    }

    public static User newUser(NewUserAddRequest newUserAddRequest) {
        User user = new User();
        user.setName(newUserAddRequest.getName());
        user.setEmail(newUserAddRequest.getEmail());
        return user;
    }

    public static void updateUser(User user, UpdateUserRequest updateUserRequest) {
        if (updateUserRequest.hasEmail()) {
            user.setEmail(updateUserRequest.getEmail());
        }
        if (updateUserRequest.hasName()) {
            user.setName(updateUserRequest.getName());
        }
    }

}