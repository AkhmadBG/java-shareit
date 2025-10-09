package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.UserClient;
import ru.practicum.shareit.user.dto.NewUserAddRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.util.AppValidation;
import ru.practicum.shareit.util.PageResponse;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> addUser(@RequestBody NewUserAddRequest newUserAddRequest) {
        AppValidation.userValidator(newUserAddRequest);
        log.info("UserController: Создание пользователя, запрос={}", newUserAddRequest);
        return userClient.addUser(newUserAddRequest);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable(value = "userId") Long userId,
                                             @RequestBody UpdateUserRequest updateUserRequest) {
        log.info("UserController: Обновление пользователя, userId={}, запрос={}", userId, updateUserRequest);
        return userClient.updateUserDto(userId, updateUserRequest);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable(value = "userId") Long userId) {
        log.info("UserController: Получение пользователя по id, userId={}", userId);
        return userClient.getUserDtoById(userId);
    }

    @GetMapping
    public ResponseEntity<PageResponse<Object>> getAllUsers(@RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "10") int size) {
        return userClient.getAllUsers(page, size);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable(value = "userId") Long userId) {
        log.info("UserController: Удаление пользователя, userId={}", userId);
        return userClient.deleteUser(userId);
    }

}