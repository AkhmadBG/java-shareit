package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.NewUserAddRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> addUser(@RequestBody NewUserAddRequest newUserAddRequest) {
        log.info("UserController: Создание пользователя, запрос={}", newUserAddRequest);
        UserDto userDto = userService.addUser(newUserAddRequest);
        log.info("UserController: Пользователь создан, userId={}", userDto.getId());
        return ResponseEntity.ok(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserDto> updateUser(@PathVariable(value = "userId") Long userId,
                                              @RequestBody UpdateUserRequest updateUserRequest) {
        log.info("UserController: Обновление пользователя, userId={}, запрос={}", userId, updateUserRequest);
        UserDto userDto = userService.updateUserDto(userId, updateUserRequest);
        log.info("UserController: Пользователь обновлён, userId={}", userDto.getId());
        return ResponseEntity.ok(userDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable(value = "userId") Long userId) {
        log.info("UserController: Получение пользователя по id, userId={}", userId);
        UserDto userDto = userService.getUserDtoById(userId);
        log.info("UserController: Пользователь найден, userId={}", userDto.getId());
        return ResponseEntity.ok(userDto);
    }

    @GetMapping
    public ResponseEntity<Page<UserDto>> getAllUsers(@RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "10") int size) {
        log.info("UserController: Получение списка пользователей, страница={}, размер={}", page, size);
        Page<UserDto> usersPage = userService.getAllUsersDto(page, size);
        log.info("UserController: Пользователи получены, количество на странице={}", usersPage.getNumberOfElements());
        return ResponseEntity.ok(usersPage);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable(value = "userId") Long userId) {
        log.info("UserController: Удаление пользователя, userId={}", userId);
        userService.deleteUser(userId);
        log.info("UserController: Пользователь удалён, userId={}", userId);
        return ResponseEntity.noContent().build();
    }

}