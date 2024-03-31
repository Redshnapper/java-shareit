package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.util.Constants;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping()
    public BookingDto create(@Valid @RequestBody BookingCreateDto createDto,
                             @RequestHeader(Constants.USER_HEADER_ID) Long userId) {
        log.info("Добавление нового букинга: {}, пользователем: {}", createDto, userId);
        return bookingService.addBooking(createDto, userId);
    }

    @PatchMapping("{bookingId}")
    public BookingDto setApprove(@RequestHeader(Constants.USER_HEADER_ID) Long userId,
                                 @RequestParam(name = "approved") Boolean approved,
                                 @PathVariable Long bookingId) {
        log.info("Запрос на смену статуса бронирования: userId = {}, approved = {}, bookingId = {}",
                userId, approved, bookingId);
        return bookingService.setApprove(userId, approved, bookingId);
    }

    @GetMapping("{bookingId}")
    public BookingDto getBookingById(@RequestHeader(Constants.USER_HEADER_ID) Long userId, @PathVariable Long bookingId) {
        log.info("Запрос на получение бронирования: userId = {}, bookingId = {}", userId, bookingId);
        return bookingService.getById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getAllByUserId(@RequestHeader(Constants.USER_HEADER_ID) Long userId,
                                           @RequestParam(name = "state", required = false, defaultValue = "ALL") String state,
                                           @RequestParam(name = "from", required = false, defaultValue = "0") Long from,
                                           @RequestParam(name = "size", required = false, defaultValue = "10") Long size) {
        log.info("Получение списка бронирований для пользователя userId = {}, state = {}, from = {}, size = {} ", userId, state, from, size);
        return bookingService.getAllByUserId(userId, state, from, size);
    }

    @GetMapping("owner")
    public List<BookingDto> getAllByOwnerId(@RequestHeader(Constants.USER_HEADER_ID) Long ownerId,
                                            @RequestParam(name = "from", required = false, defaultValue = "0") Long from,
                                            @RequestParam(name = "size", required = false, defaultValue = "10") Long size,
                                            @RequestParam(name = "state", required = false, defaultValue = "ALL") String state) {
        log.info("Получение списка бронирований для владельца ownerId = {}, state = {}, from = {}, size = {} ", ownerId, state, from, size);
        return bookingService.getAllByOwnerId(ownerId, state, from, size);
    }

}

