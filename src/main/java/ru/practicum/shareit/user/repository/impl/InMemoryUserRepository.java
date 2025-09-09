package ru.practicum.shareit.user.repository.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class InMemoryUserRepository implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();
    private Long identifier = 0L;

    @Override
    public User addUser(User user) {
        List<String> usersEmails = users.values().stream()
                .map(User::getEmail)
                .toList();
        if (usersEmails.contains(user.getEmail())) {
            throw new ConflictException("пользователь с такой электронной почтой " + user.getEmail()
                    + " уже существует");
        }
        users.put(getIdentifier(), user);
        user.setId(identifier);
        log.info("InMemoryUserRepository: добавлен новый пользователь с id = {}", user.getId());
        return users.get(identifier);
    }

    @Override
    public User getUserById(Long userId) {
        User user = users.get(userId);
        log.info("InMemoryUserRepository: получение пользователя с id = {}", userId);
        return user;
    }

    @Override
    public void updateUser(User user) {
        Collection<User> usersList = users.values();
        for (User checkUser : usersList) {
            if (checkUser.getEmail().equals(user.getEmail()) &&
                    !checkUser.getId().equals(user.getId())) {
                throw new ConflictException("пользователь с такой электронной почтой " + user.getEmail()
                        + " уже существует");
            }
        }
        users.put(user.getId(), user);
        log.info("InMemoryUserRepository: пользователь с id = {} обновлен", user.getId());
    }

    @Override
    public List<User> getAllUsers() {
        List<User> allUsers = (List<User>) users.values();
        log.info("InMemoryUserRepository: получение списка всех пользователей, количество пользователей = {}", allUsers.size());
        return allUsers;
    }

    @Override
    public void deleteUser(Long userId) {
        users.remove(userId);
        log.info("InMemoryUserRepository: удаление пользователя с id = {}", userId);
    }

    private Long getIdentifier() {
        return ++identifier;
    }

}