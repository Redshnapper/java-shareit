package ru.practicum.shareit.user.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void testAddUser() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("TestUser");
        userDto.setEmail("test@example.com");
        UserDto expectedDto = new UserDto();
        expectedDto.setId(1L);
        expectedDto.setName(userDto.getName());
        expectedDto.setEmail(userDto.getEmail());
        when(userService.addUser(any(UserDto.class))).thenReturn(expectedDto);

        mockMvc.perform(post("/users")
                        .content("{\"name\":\"TestUser\",\"email\":\"test@example.com\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("TestUser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void testUpdateUser() throws Exception {
        Long userId = 1L;
        UserDto userDto = new UserDto();
        userDto.setName("UpdatedUser");
        userDto.setEmail("updated@example.com");

        when(userService.updateUser(any(UserDto.class), eq(userId))).thenReturn(userDto);
        mockMvc.perform(patch("/users/{id}", userId)
                        .content("{\"name\":\"UpdatedUser\",\"email\":\"updated@example.com\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("UpdatedUser"))
                .andExpect(jsonPath("$.email").value("updated@example.com"));
    }

    @Test
    void testDeleteUserById() throws Exception {
        Long userId = 1L;
        mockMvc.perform(delete("/users/{id}", userId))
                .andExpect(status().isOk());
        verify(userService, times(1)).deleteUserById(userId);
    }

    @Test
    void testGetAllUsers() throws Exception {
        List<UserDto> userList = new ArrayList<>();
        when(userService.getAllUsers()).thenReturn(userList);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(userList.size())));
    }

    @Test
    void testGetUserById() throws Exception {
        Long userId = 1L;
        UserDto userDto = new UserDto();
        userDto.setId(userId);
        when(userService.getUserById(userId)).thenReturn(userDto);

        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userId));
    }
}