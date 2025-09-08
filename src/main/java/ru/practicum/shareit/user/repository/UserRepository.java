package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {

    User addUser(User user);

    User getUserById(Long id);

    void updateUser(User user);

    List<User> getAllUsers();

    void deleteUser(Long userId);

}