package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    User add(User user);

    List<User> getAll();

    User update(User user);

    User getById(Long id);

    void deleteById(Long id);
}
