package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
        User user = getUser(userId);
        checkItemOwner(user, booking);
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
    public List<BookingDto> getAllByUserId(Long userId, String state, Long from, Long size) {
        if (from < 0 || size <= 0) {
            throw new BadRequestException();
        }

        getUser(userId);
        Pageable pageable = PageRequest.of(from.intValue() / size.intValue(), size.intValue());

        List<Booking> bookings;
        switch (state) {
            case "ALL":
                bookings = bookingRepository.findBookingByBookerIdOrderByStartDesc(userId, pageable);
                break;
            case "FUTURE":
                bookings = bookingRepository.findBookingByBookerIdAndStartAfterOrderByStartDesc(userId,
                        LocalDateTime.now(), pageable);
                break;
            case "REJECTED":
                bookings = bookingRepository.findBookingByBookerIdAndStatusOrderByStartDesc(userId,
                        BookingStatus.REJECTED, pageable);
                break;
            case "WAITING":
                bookings = bookingRepository.findBookingByBookerIdAndStatusOrderByStartDesc(userId,
                        BookingStatus.WAITING, pageable);
                break;
            case "CURRENT":
                LocalDateTime now = LocalDateTime.now();
                bookings = bookingRepository.findBookingByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId,
                        now, now, pageable);
                break;
            case "PAST":
                bookings = bookingRepository.findBookingByBookerIdAndEndBeforeOrderByStartDesc(userId,
                        LocalDateTime.now(), pageable);
                break;
            default:
                throw new BadRequestException("Unknown state: " + state);
        }

        return bookings.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }


    @Override
    public List<BookingDto> getAllByOwnerId(Long ownerId, String state, Long from, Long size) {
        if (from < 0 || size <= 0) {
            throw new BadRequestException();
        }

        getUser(ownerId);
        List<Item> itemsByOwnerId = itemRepository.findItemsByOwnerIdOrderById(ownerId);
        List<Booking> collect = bookingRepository.findAll().stream()
                .sorted(Comparator.comparing(Booking::getStart).reversed())
                .filter(booking -> itemsByOwnerId.contains(booking.getItem()))
                .collect(Collectors.toList());

        int fromIndex = from.intValue();
        int toIndex = Math.min(fromIndex + size.intValue(), collect.size());
        List<Booking> paginatedList = collect.subList(fromIndex, toIndex);

        switch (state) {
            case "ALL":
                return paginatedList.stream().map(mapper::toDto).collect(Collectors.toList());
            case "FUTURE":
                return paginatedList.stream().filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                        .map(mapper::toDto).collect(Collectors.toList());
            case "REJECTED":
                return paginatedList.stream().filter(booking -> booking.getStatus() == BookingStatus.REJECTED)
                        .map(mapper::toDto).collect(Collectors.toList());
            case "WAITING":
                return paginatedList.stream().filter(booking -> booking.getStatus() == BookingStatus.WAITING)
                        .map(mapper::toDto).collect(Collectors.toList());
            case "CURRENT":
                LocalDateTime now = LocalDateTime.now();
                return paginatedList.stream().filter(booking -> booking.getStart().isBefore(now)
                        && booking.getEnd().isAfter(now)).map(mapper::toDto).collect(Collectors.toList());
            case "PAST":
                return paginatedList.stream().filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
                        .map(mapper::toDto).collect(Collectors.toList());
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

    private void checkItemOwner(User user, Booking booking) {
        if (booking.getBooker().equals(user)) {
            throw new NotFoundException("У вас нет прав менять статус вещи!");
        }
        if (!booking.getItem().getOwner().equals(user)) {
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

    public Booking getBooking(Long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Booking not found"));
    }

    public Booking create(Long itemId, Long userId, BookingCreateDto createDto) {
        final Booking booking = mapper.toBooking(createDto);
        final User booker = getUser(userId);
        final Item item = getItem(itemId);
        booking.setStatus(BookingStatus.WAITING);
        booking.setItem(item);
        booking.setBooker(booker);
        return booking;
    }

}
