package ru.practicum.shareit.booking.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Test
    public void testCreateBooking_ValidData() throws Exception {
        BookingCreateDto createDto = new BookingCreateDto();
        LocalDateTime currentTime = LocalDateTime.now();
        createDto.setStart(currentTime.plusHours(1));
        createDto.setEnd(currentTime.plusHours(2));
        createDto.setItemId(1L);
        Long userId = 123L;
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStart(createDto.getStart());
        bookingDto.setEnd(createDto.getEnd());
        bookingDto.setStatus(BookingStatus.WAITING);
        bookingDto.setBooker(new UserDto());
        bookingDto.setItem(new ItemDto());

        when(bookingService.addBooking(any(BookingCreateDto.class), any(Long.class))).thenReturn(bookingDto);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS"); // (7S) чтобы работало локально, (6S) для гита))
        String expectedStart = createDto.getStart().format(formatter);
        String expectedEnd = createDto.getEnd().format(formatter);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .content("{\"start\":\"" + expectedStart + "\",\"end\":\"" + expectedEnd + "\",\"itemId\":1}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.start").value(expectedStart))
                .andExpect(jsonPath("$.end").value(expectedEnd))
                .andExpect(jsonPath("$.booker").isMap())
                .andExpect(jsonPath("$.item").isMap());
    }

    @Test
    public void testSetApprove_Success() throws Exception {
        Long userId = 123L;
        Boolean approved = true;
        Long bookingId = 1L;
        BookingDto expectedBookingDto = new BookingDto();
        expectedBookingDto.setId(bookingId);
        expectedBookingDto.setStatus(BookingStatus.APPROVED);

        when(bookingService.setApprove(userId, approved, bookingId)).thenReturn(expectedBookingDto);
        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", String.valueOf(approved)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    public void testGetBookingById_Success() throws Exception {
        Long userId = 123L;
        Long bookingId = 1L;
        BookingDto expectedBookingDto = new BookingDto();
        expectedBookingDto.setId(bookingId);
        when(bookingService.getById(userId, bookingId)).thenReturn(expectedBookingDto);

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId));
    }

    @Test
    public void testGetAllByUserId_Success() throws Exception {
        Long userId = 123L;
        String state = "ALL";
        Long from = 0L;
        Long size = 10L;
        List<BookingDto> expectedBookingList = new ArrayList<>();
        BookingDto booking = new BookingDto();
        booking.setId(1L);
        expectedBookingList.add(booking);

        when(bookingService.getAllByUserId(userId, state, from, size)).thenReturn(expectedBookingList);
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", state)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(expectedBookingList.size())))
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    public void testGetAllByOwnerId_Success() throws Exception {
        Long ownerId = 123L;
        Long from = 0L;
        Long size = 10L;
        String state = "ALL";
        List<BookingDto> expectedBookingList = new ArrayList<>();
        BookingDto booking = new BookingDto();
        booking.setId(1L);
        expectedBookingList.add(booking);

        when(bookingService.getAllByOwnerId(ownerId, state, from, size)).thenReturn(expectedBookingList);
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", ownerId)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                        .param("state", state))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(expectedBookingList.size())))
                .andExpect(jsonPath("$[0].id").value(1L));
    }

}