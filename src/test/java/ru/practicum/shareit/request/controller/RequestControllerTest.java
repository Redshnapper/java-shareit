package ru.practicum.shareit.request.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.RequestCreateDto;
import ru.practicum.shareit.request.dto.RequestItemsDto;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.util.Constants;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = RequestController.class)
class RequestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RequestService requestService;

    @Test
    void testCreateRequest() throws Exception {
        Long userId = 123L;
        RequestCreateDto createDto = new RequestCreateDto();
        createDto.setDescription("Test description");

        RequestCreateDto expectedDto = new RequestCreateDto();
        expectedDto.setId(1L);
        expectedDto.setDescription(createDto.getDescription());

        when(requestService.createRequest(any(RequestCreateDto.class), eq(userId))).thenReturn(expectedDto);

        mockMvc.perform(post("/requests")
                        .header(Constants.USER_HEADER_ID, userId)
                        .content("{\"description\":\"Test description\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Test description"));
    }


    @Test
    void testGetRequests() throws Exception {
        Long userId = 123L;
        List<RequestItemsDto> expectedList = new ArrayList<>();
        RequestItemsDto request1 = new RequestItemsDto();
        request1.setId(1L);
        expectedList.add(request1);

        when(requestService.getRequests(userId)).thenReturn(expectedList);
        mockMvc.perform(get("/requests")
                        .header(Constants.USER_HEADER_ID, userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(expectedList.size())))
                .andExpect(jsonPath("$[0].id").value(1L));
    }


    @Test
    void testRequestsGetAll() throws Exception {
        Long userId = 123L;
        Long from = 0L;
        Long size = 10L;
        List<RequestItemsDto> expectedList = new ArrayList<>();
        RequestItemsDto request1 = new RequestItemsDto();
        request1.setId(1L);
        expectedList.add(request1);

        when(requestService.requestsGetAll(userId, from, size)).thenReturn(expectedList);
        mockMvc.perform(get("/requests/all")
                        .header(Constants.USER_HEADER_ID, userId)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(expectedList.size())))
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void testGetRequestById() throws Exception {
        Long userId = 123L;
        Long requestId = 1L;
        RequestItemsDto expectedDto = new RequestItemsDto();
        expectedDto.setId(requestId);
        when(requestService.getRequestById(userId, requestId)).thenReturn(expectedDto);
        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header(Constants.USER_HEADER_ID, userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(requestId));
    }

}