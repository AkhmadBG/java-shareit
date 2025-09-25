package ru.practicum.shareit.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.*;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.AppValidation;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapStruct userMapStruct;

    @Override
    public UserDto addUser(NewUserAddRequest newUserAddRequest) {
        log.info("UserServiceImpl: Создание пользователя, запрос={}", newUserAddRequest);
        AppValidation.userValidator(newUserAddRequest);
        User user = userRepository.save(userMapStruct.newUser(newUserAddRequest));
        log.info("UserServiceImpl: Пользователь создан, userId={}", user.getId());
        return userMapStruct.toUserDto(user);
    }

    @Override
    public UserDto updateUserDto(Long userId, UpdateUserRequest updateUserRequest) {
        log.info("UserServiceImpl: Обновление пользователя, userId={}, запрос={}", userId, updateUserRequest);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("UserServiceImpl: пользователь с id = " + userId + " не найден"));
        userMapStruct.updateUser(user, updateUserRequest);
        userRepository.save(user);
        log.info("UserServiceImpl: Пользователь обновлён, userId={}", user.getId());
        return userMapStruct.toUserDto(user);
    }

    @Override
    public UserDto getUserDtoById(Long userId) {
        log.info("UserServiceImpl: Поиск пользователя по id, userId={}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("UserServiceImpl: пользователь с id = " + userId + " не найден"));
        log.info("UserServiceImpl: Пользователь найден, userId={}", user.getId());
        return userMapStruct.toUserDto(user);
    }

    @Override
    public Page<UserDto> getAllUsersDto(int page, int size) {
        log.info("UserServiceImpl: Получение списка пользователей, страница={}, размер={}", page, size);
        Page<User> userPage = userRepository.findAll(PageRequest.of(page, size));
        log.info("UserServiceImpl: Пользователи получены, количество на странице={}", userPage.getNumberOfElements());
        return userPage.map(userMapStruct::toUserDto);
    }

    @Override
    public void deleteUser(Long userId) {
        log.info("UserServiceImpl: Удаление пользователя, userId={}", userId);
        userRepository.deleteById(userId);
        log.info("UserServiceImpl: Пользователь удалён, userId={}", userId);
    }

    @Override
    public User getUserById(Long userId) {
        log.info("UserServiceImpl: Поиск пользователя по id, userId={}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("UserServiceImpl: пользователь с id = " + userId + " не найден"));
        log.info("UserServiceImpl: Пользователь найден, userId={}", user.getId());
        return user;
    }

}