package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.InMemoryUserRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final InMemoryUserRepository memoryUserRepository;
    private final UserMapper mapper;

    @Override
    public UserDto addUser(UserDto userDto) {
        final User user = mapper.toUser(userDto);
        log.info("Добавление нового пользователя {}", user);
        return mapper.toDto(userRepository.save(user));
    }

    @Override
    public List<UserDto> getAllUsers() {
        log.info("Получение всех пользователей");
        return userRepository.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    public UserDto updateUser(UserDto userDto, Long id) {
        final User user = mapper.toUser(userDto);
        log.info("Обновление пользователя с id {} : {}", id, user);
        user.setId(id);
        User savedUser = userRepository.getReferenceById(id);
        User updateUser = memoryUserRepository.checkUpdatesAndUpdateUser(user, savedUser);
        return mapper.toDto(userRepository.save(updateUser));
    }

    @Override
    public UserDto getUserById(Long id) {
        log.info("Получение пользователя по id: {}", id);
        try {
            User user = userRepository.getReferenceById(id);
            return mapper.toDto(user);
        } catch (Exception e) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }

    }

    @Override
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

}
