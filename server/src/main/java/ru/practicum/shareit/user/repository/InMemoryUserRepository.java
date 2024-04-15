package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class InMemoryUserRepository {
    private final Map<Long, User> users = new HashMap<>();
    protected static Long idGenerator = 1L;


    public User add(User user) {
        if (checkEmailNotExist(user)) {
            throw new BadRequestException("Емайл уже используется");
        }
        user.setId(idGenerator++);
        users.put(user.getId(), user);
        return user;
    }


    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }


    public User update(User user) {
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException("Пользователь с id " + user.getId() + " не найден");
        }
        if (checkEmailNotExist(user)) {
            throw new BadRequestException("Емайл уже используется");
        }
        User savedUser = users.get(user.getId());
        User updatedUser = checkUpdatesAndUpdateUser(user, savedUser);
        users.put(updatedUser.getId(), updatedUser);
        return updatedUser;
    }

    public User getById(Long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        return users.get(id);
    }


    public void deleteById(Long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        users.remove(id);
    }

    private boolean checkEmailNotExist(User userForCheck) {
        boolean check = users.values().stream()
                .noneMatch(user -> user.getEmail().equals(userForCheck.getEmail()));
        return !check;
    }

    public User checkUpdatesAndUpdateUser(User user, User updatedUser) {
        String name = user.getName();
        String email = user.getEmail();

        if (name != null) {
            updatedUser.setName(name);
        }
        if (email != null) {
            updatedUser.setEmail(email);
        }
        return updatedUser;
    }

    public void removeAll() {
        idGenerator = 1L;
        users.clear();
    }
}
