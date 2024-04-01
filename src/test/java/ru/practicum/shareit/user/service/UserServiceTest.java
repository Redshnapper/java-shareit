package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.InMemoryUserRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    private final InMemoryUserRepository memoryRepository = new InMemoryUserRepository();
    private UserMapper mapper;
    private UserService service;

    @BeforeEach
    public void setUp() {
        mapper = new UserMapper() {
            public User toUser(UserDto userDto) {
                User.UserBuilder user = User.builder();
                user.id(userDto.getId());
                user.name(userDto.getName());
                user.email(userDto.getEmail());
                return user.build();
            }

            public UserDto toDto(User user) {
                UserDto userDto = new UserDto();
                userDto.setId(user.getId());
                userDto.setName(user.getName());
                userDto.setEmail(user.getEmail());
                return userDto;
            }
        };

        service = new UserServiceImpl(
                userRepository,
                memoryRepository,
                mapper
        );
    }

    @Test
    void addUser() {
        User user = User.builder()
                .id(1L)
                .name("name")
                .email("email@mail.com")
                .build();
        when(userRepository.save(any(User.class))).thenReturn(user);
        UserDto added = service.addUser(mapper.toDto(user));

        assertNotNull(added);
        assertEquals(user.getId(), added.getId());
        assertEquals(user.getName(), added.getName());
        assertEquals(user.getEmail(), added.getEmail());
    }

    @Test
    void getAllUsers() {
        User user = User.builder()
                .id(1L)
                .name("name")
                .email("email@mail.com")
                .build();
        User user2 = User.builder()
                .id(2L)
                .name("name 2")
                .email("email2@mail.com")
                .build();
        when(userRepository.findAll()).thenReturn(List.of(user, user2));

        List<UserDto> users = service.getAllUsers();

        assertNotNull(users);
        assertEquals(2, users.size());
        assertEquals(1L, users.get(0).getId());
        assertEquals(2L, users.get(1).getId());
        assertEquals(user.getName(), users.get(0).getName());
        assertEquals(user2.getName(), users.get(1).getName());
        assertEquals(user.getEmail(), users.get(0).getEmail());
        assertEquals(user2.getEmail(), users.get(1).getEmail());
    }

    @Test
    void updateUser() {
        User user = User.builder()
                .id(1L)
                .name("name")
                .email("email@mail.com")
                .build();
        User updated = User.builder()
                .name("up")
                .email("dated@mail.com")
                .build();

        when(userRepository.getReferenceById(1L)).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(updated);

        UserDto save = service.updateUser(mapper.toDto(updated), 1L);

        assertNotNull(save);
        assertEquals(updated.getId(), save.getId());
        assertEquals(updated.getName(), save.getName());
        assertEquals(updated.getEmail(), save.getEmail());
    }

    @Test
    void updateUserNoChanges() {
        User user = User.builder()
                .id(1L)
                .name("name")
                .email("email@mail.com")
                .build();
        User updated = User.builder()
                .build();

        when(userRepository.getReferenceById(1L)).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto save = service.updateUser(mapper.toDto(updated), 1L);

        assertNotNull(save);
        assertEquals(user.getId(), save.getId());
        assertEquals(user.getName(), save.getName());
        assertEquals(user.getEmail(), save.getEmail());
    }

    @Test
    void getUserByIdCorrect() {
        User user = new User(1L);
        when(userRepository.getReferenceById(user.getId())).thenReturn(user);
        UserDto save = service.getUserById(user.getId());
        assertNotNull(save);
        assertEquals(user.getId(), save.getId());
        assertEquals(user.getName(), save.getName());
        assertEquals(user.getEmail(), save.getEmail());
    }

    @Test
    void getUserByIdThrowsNotFoundUserException() {
        Throwable exception = assertThrows(NotFoundException.class, () -> service.getUserById(1L));
        assertEquals("Пользователь с id " + 1L + " не найден", exception.getMessage());
    }

    @Test
    void deleteUserById() {
        User user = new User(1L);
        doNothing().when(userRepository).deleteById(user.getId());
        service.deleteUserById(user.getId());
        verify(userRepository, times(1)).deleteById(user.getId());
    }
}