package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto addBooking(BookingCreateDto bookingDto, Long userId);

    BookingDto setApprove(Long userId, Boolean approved, Long bookingId);

    BookingDto getById(Long userId, Long bookingId);

    List<BookingDto> getAllByUserId(Long userId, String state, Long from, Long size);

    List<BookingDto> getAllByOwnerId(Long userId, String state, Long from, Long size);
}

