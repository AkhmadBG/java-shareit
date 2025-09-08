package ru.practicum.shareit.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.NewUserAddRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.AppValidation;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto addUser(NewUserAddRequest newUserAddRequest) {
        AppValidation.userValidator(newUserAddRequest);
        User user = userRepository.addUser(UserMapper.newUser(newUserAddRequest));
        log.info("UserServiceImpl: добавлен новый пользователь с id = {}", user.getId());
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(Long userId, UpdateUserRequest updateUserRequest) {
        User user = userRepository.getUserById(userId);
        UserMapper.updateUser(user, updateUserRequest);
        userRepository.updateUser(user);
        log.info("UserServiceImpl: пользователь с id = {} обновлен", user.getId());
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto getUserById(Long userId) {
        User user = userRepository.getUserById(userId);
        log.info("UserServiceImpl: получение пользователя с id = {}", user.getId());
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> allUsers = userRepository.getAllUsers();
        log.info("UserServiceImpl: получение списка всех пользователей, количество пользователей = {}", allUsers.size());
        return allUsers.stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteUser(userId);
        log.info("UserServiceImpl: удаление пользователя с id = {}", userId);
    }

}