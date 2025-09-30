package ru.practicum.shareit.user.service;

import org.springframework.data.domain.Page;
import ru.practicum.shareit.user.dto.NewUserAddRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public interface UserService {

    UserDto addUser(NewUserAddRequest newUserAddRequest);

    UserDto updateUserDto(Long userId, UpdateUserRequest updateUserRequest);

    UserDto getUserDtoById(Long userId);

    Page<UserDto> getAllUsersDto(int page, int size);

    void deleteUser(Long userId);

    User getUserById(Long userId);

}