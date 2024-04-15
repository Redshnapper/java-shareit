package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> addUser(@RequestBody @Valid UserDto userDto) {
        log.info("Adding user: {}", userDto);
        return userClient.addUser(userDto);
    }

    @PatchMapping("{id}")
    public ResponseEntity<Object> updateUser(@RequestBody UserDto userDto, @PathVariable Long id) {
        log.info("Updating user with id {}: {}", id, userDto);
        return userClient.updateUser(userDto, id);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> deleteUserById(@PathVariable Long id) {
        log.info("Deleting user with id: {}", id);
        return userClient.deleteUser(id);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Getting all users");
        return userClient.getAllUsers();
    }

    @GetMapping("{id}")
    public ResponseEntity<Object> getUserById(@PathVariable Long id) {
        log.info("Getting user by id: {}", id);
        return userClient.getUserById(id);
    }
}
