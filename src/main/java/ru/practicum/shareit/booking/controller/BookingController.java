package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private final BookingRepository bookingRepository;

    @PostMapping()
    public BookingDto create(@Valid @RequestBody BookingCreateDto bookingDto,
                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.addBooking(bookingDto, userId);
    }

    @PatchMapping("{bookingId}")
    public BookingDto setApprove(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @RequestParam(name = "approved") Boolean approved,
                                 @PathVariable Long bookingId) {
        return bookingService.setApprove(userId, approved, bookingId);
    }

    @GetMapping("{bookingId}")
    public BookingDto getBookingById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId) {
        return bookingService.getById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getAllByUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @RequestParam(name = "state", required = false, defaultValue = "ALL") String state) {
        return bookingService.getAllByUserId(userId, state);
    }

    @GetMapping("owner")
    public List<BookingDto> getAllByOwnerId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @RequestParam(name = "state", required = false, defaultValue = "ALL") String state) {
        return bookingService.getAllByOwnerId(userId, state);
    }

}

