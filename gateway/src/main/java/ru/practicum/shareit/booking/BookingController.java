package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.BadRequestException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(@Valid @RequestBody BookingCreateDto createDto,
                                                @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Creating booking: {}, user: {}", createDto, userId);
        return bookingClient.createBooking(userId, createDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> setApprove(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestParam(name = "approved") Boolean approved,
                                             @PathVariable Long bookingId) {
        log.info("Requesting approval status change for booking: userId = {}, approved = {}, bookingId = {}",
                userId, approved, bookingId);
        return bookingClient.setApprove(userId, approved, bookingId);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @PathVariable Long bookingId) {
        log.info("Requesting booking details: userId = {}, bookingId = {}", userId, bookingId);
        return bookingClient.getBookingById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @RequestParam(name = "state", required = false, defaultValue = "ALL") String stateParam,
                                                 @PositiveOrZero @RequestParam(name = "from", required = false, defaultValue = "0") Long from,
                                                 @Positive @RequestParam(name = "size", required = false, defaultValue = "10") Long size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new BadRequestException("Unknown state: " + stateParam));
        log.info("Retrieving bookings for user: userId = {}, state = {}, from = {}, size = {}", userId, state, from, size);
        return bookingClient.getAllByUserId(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllByOwnerId(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                  @PositiveOrZero @RequestParam(name = "from", required = false, defaultValue = "0") Long from,
                                                  @Positive @RequestParam(name = "size", required = false, defaultValue = "10") Long size,
                                                  @RequestParam(name = "state", required = false, defaultValue = "ALL") String stateParam) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new BadRequestException("Unknown state: " + stateParam));
        log.info("Retrieving bookings for owner: ownerId = {}, state = {}, from = {}, size = {}", ownerId, state, from, size);
        return bookingClient.getAllByOwnerId(ownerId, from, size, state);
    }

}

