package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.NewUserAddRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto addUser(NewUserAddRequest newUserAddRequest);

    UserDto updateUser(Long userId, UpdateUserRequest updateUserRequest);

    UserDto getUserById(Long userId);

    void deleteUser(Long userId);

    List<UserDto> getAllUsers();

}