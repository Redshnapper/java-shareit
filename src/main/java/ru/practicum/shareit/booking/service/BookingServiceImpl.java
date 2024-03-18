package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final BookingMapper mapper;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public BookingDto addBooking(BookingCreateDto createDto, Long userId) {
        Long itemId = createDto.getItemId();
        if (!checkItemAvailable(itemId)) {
            throw new BadRequestException("Предмет не доступен для бронирования");
        }
        final Booking booking = create(itemId, userId, createDto);
        if (userId.equals(booking.getItem().getOwner().getId())) {
            throw new NotFoundException("Владелец вещи не может ее бронировать)");
        }
        return mapper.toDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto setApprove(Long userId, Boolean approved, Long bookingId) {
        Booking booking = getBooking(bookingId);
        User owner = getUser(userId);
        checkItemOwner(owner, booking);
        setBookingStatus(approved, booking);
        return mapper.toDto(bookingRepository.save(booking));
    }


    @Override
    public BookingDto getById(Long userId, Long bookingId) {
        Booking booking = getBooking(bookingId);
        Long bookerId = booking.getBooker().getId();
        Long ownerId = booking.getItem().getOwner().getId();
        if (!userId.equals(bookerId) && !userId.equals(ownerId)) {
            throw new NotFoundException("У вас нет прав смотреть данное бронирование");
        }
        return mapper.toDto(booking);
    }

    @Override
    public List<BookingDto> getAllByUserId(Long userId, String state) {
        getUser(userId);
        switch (state) {
            case "ALL":
                return bookingRepository.findBookingByBookerIdOrderByStartDesc(userId)
                        .stream()
                        .map(mapper::toDto)
                        .collect(Collectors.toList());
            case "FUTURE":
                return bookingRepository.findBookingByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now())
                        .stream()
                        .map(mapper::toDto)
                        .collect(Collectors.toList());
            case "REJECTED":
                return bookingRepository.findBookingByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED)
                        .stream()
                        .map(mapper::toDto)
                        .collect(Collectors.toList());
            case "WAITING":
                return bookingRepository.findBookingByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING)
                        .stream()
                        .map(mapper::toDto)
                        .collect(Collectors.toList());
            case "CURRENT":
                return bookingRepository.findBookingByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now())
                        .stream()
                        .map(mapper::toDto)
                        .collect(Collectors.toList());
            case "PAST":
                return bookingRepository.findBookingByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now())
                        .stream()
                        .map(mapper::toDto)
                        .collect(Collectors.toList());
            default:
                throw new BadRequestException("Unknown state: " + state);
        }
    }

    @Override
    public List<BookingDto> getAllByOwnerId(Long ownerId, String state) {
        getUser(ownerId);

        List<Item> itemsByOwnerId = itemRepository.findItemsByOwnerIdOrderById(ownerId);
        List<Booking> collect = bookingRepository.findAll()
                .stream()
                .filter(booking -> itemsByOwnerId.contains(booking.getItem()))
                .collect(Collectors.toList());

        switch (state) {
            case "ALL":
                return collect.stream()
                        .sorted(Comparator.comparing(Booking::getStart).reversed())
                        .map(mapper::toDto)
                        .collect(Collectors.toList());
            case "FUTURE":
                return collect.stream()
                        .sorted(Comparator.comparing(Booking::getStart).reversed())
                        .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                        .map(mapper::toDto)
                        .collect(Collectors.toList());
            case "REJECTED":
                return collect.stream()
                        .sorted(Comparator.comparing(Booking::getStart).reversed())
                        .filter(booking -> booking.getStatus().equals(BookingStatus.REJECTED))
                        .map(mapper::toDto)
                        .collect(Collectors.toList());
            case "WAITING":
                return collect.stream()
                        .sorted(Comparator.comparing(Booking::getStart).reversed())
                        .filter(booking -> booking.getStatus().equals(BookingStatus.WAITING))
                        .map(mapper::toDto)
                        .collect(Collectors.toList());
            case "CURRENT":
                return collect.stream()
                        .sorted(Comparator.comparing(Booking::getStart).reversed())
                        .filter(booking -> booking.getStart().isBefore(LocalDateTime.now())
                                && booking.getEnd().isAfter(LocalDateTime.now()))
                        .map(mapper::toDto)
                        .collect(Collectors.toList());
            case "PAST":
                return collect.stream()
                        .sorted(Comparator.comparing(Booking::getStart).reversed())
                        .filter(booking -> LocalDateTime.now().isAfter(booking.getEnd()))
                        .map(mapper::toDto)
                        .collect(Collectors.toList());
            default:
                throw new BadRequestException("Unknown state: " + state);
        }
    }

    private void setBookingStatus(Boolean approved, Booking booking) {
        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new BadRequestException();
        }
        if (approved.equals(true)) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
    }

    private void checkItemOwner(User owner, Booking booking) {
        if (booking.getBooker().equals(owner)) {
            throw new NotFoundException("У вас нет прав менять статус вещи!");
        }
        if (!booking.getItem().getOwner().equals(owner)) {
            throw new BadRequestException("Только владелец может менять статус бронирования");
        }
    }

    private Item getItem(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Item not found"));
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
    }

    private boolean checkItemAvailable(Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Item not found"));
        return item.getAvailable();
    }

    private Booking getBooking(Long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Booking not found"));
    }

    private Booking create(Long itemId, Long userId, BookingCreateDto createDto) {
        final Booking booking = mapper.toBooking(createDto);
        final User booker = getUser(userId);
        final Item item = getItem(itemId);
        booking.setStatus(BookingStatus.WAITING);
        booking.setItem(item);
        booking.setBooker(booker);
        return booking;
    }

}
