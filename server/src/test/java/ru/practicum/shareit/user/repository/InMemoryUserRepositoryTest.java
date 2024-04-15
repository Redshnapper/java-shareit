package ru.practicum.shareit.user.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryUserRepositoryTest {

    private InMemoryUserRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryUserRepository();
    }

    @AfterEach
    void clean() {
        repository.removeAll();
    }

    @Test
    void add() {
        User user = User.builder()
                .name("name")
                .email("email")
                .build();
        User add = repository.add(user);
        assertNotNull(add);
        assertEquals(1L, add.getId());
        assertEquals(user.getName(), add.getName());
        assertEquals(user.getEmail(), add.getEmail());
    }

    @Test
    void addThrowsValidationException() {
        User user = User.builder()
                .name("name")
                .email("email")
                .build();
        User user2 = User.builder()
                .name("name")
                .email("email")
                .build();
        repository.add(user);
        Throwable exception = assertThrows(BadRequestException.class, () -> repository.add(user2));
        assertEquals("Емайл уже используется", exception.getMessage());
    }

    @Test
    void getAll() {
        User user = User.builder()
                .name("name")
                .email("email")
                .build();
        User user2 = User.builder()
                .name("name2")
                .email("email2")
                .build();
        repository.add(user);
        repository.add(user2);

        List<User> all = repository.getAll();
        assertNotNull(all);
        assertEquals(2, all.size());
        assertEquals(user.getId(), all.get(0).getId());
        assertEquals(user2.getId(), all.get(1).getId());
    }

    @Test
    void updateThrowsNotFoundUserException() {
        Throwable exception = assertThrows(NotFoundException.class, () -> repository.update(new User(1L)));
        assertEquals("Пользователь с id " + 1L + " не найден", exception.getMessage());
    }

    @Test
    void updateThrowsValidationEmailException() {
        User user = User.builder()
                .name("name")
                .email("email")
                .build();
        User update = User.builder()
                .id(1L)
                .name("name2")
                .email("email")
                .build();
        repository.add(user);
        Throwable exception = assertThrows(BadRequestException.class, () -> repository.update(update));
        assertEquals("Емайл уже используется", exception.getMessage());
    }

    @Test
    void updateCorrect() {
        User user = User.builder()
                .name("name")
                .email("email")
                .build();
        User update = User.builder()
                .id(1L)
                .name("name2")
                .email("email2")
                .build();
        repository.add(user);
        User save = repository.update(update);

        assertNotNull(save);
        assertEquals(update.getId(), save.getId());
        assertEquals(update.getEmail(), save.getEmail());
        assertEquals(update.getName(), save.getName());
    }

    @Test
    void updateCorrectNullNameAndEmail() {
        User user = User.builder()
                .name("name")
                .email("email")
                .build();
        User update = User.builder()
                .id(1L)
                .name(null)
                .email(null)
                .build();
        repository.add(user);
        User save = repository.update(update);

        assertNotNull(save);
        assertEquals(user.getId(), save.getId());
        assertEquals(user.getEmail(), save.getEmail());
        assertEquals(user.getName(), save.getName());
    }

    @Test
    void getById() {
        User user = User.builder()
                .name("name")
                .email("email")
                .build();
        repository.add(user);
        User save = repository.getById(1L);

        assertNotNull(save);
        assertEquals(user.getId(), save.getId());
        assertEquals(user.getEmail(), save.getEmail());
        assertEquals(user.getName(), save.getName());
    }

    @Test
    void getByIdThrowsNotFoundUserException() {
        Throwable exception = assertThrows(NotFoundException.class, () -> repository.getById(1L));
        assertEquals("Пользователь с id " + 1L + " не найден", exception.getMessage());
    }

    @Test
    void deleteByIdThrowsNotFoundUserException() {
        Throwable exception = assertThrows(NotFoundException.class, () -> repository.deleteById(1L));
        assertEquals("Пользователь с id " + 1L + " не найден", exception.getMessage());
    }

    @Test
    void deleteByIdCorrect() {
        User user = User.builder()
                .name("name")
                .email("email")
                .build();
        repository.add(user);
        assertEquals(1, repository.getAll().size());
        repository.deleteById(1L);
        assertEquals(0, repository.getAll().size());
    }

    @Test
    void checkUpdatesAndUpdateUser() {


    }
}