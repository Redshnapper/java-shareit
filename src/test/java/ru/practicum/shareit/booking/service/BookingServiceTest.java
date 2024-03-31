package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;

    private final BookingMapper mapper = new BookingMapper();

    private BookingService bookingService;

    @BeforeEach
    public void setUp() {
        bookingService = new BookingServiceImpl(
                bookingRepository,
                mapper,
                itemRepository,
                userRepository
        );
    }


    @Test
    void testAddBookingSuccess() {
        LocalDateTime before = LocalDateTime.now().minusHours(1);
        LocalDateTime after = LocalDateTime.now().plusHours(1);
        BookingCreateDto createDto = new BookingCreateDto();
        createDto.setItemId(1L);
        createDto.setStart(before);
        createDto.setEnd(after);

        User booker = new User();
        booker.setId(1L);
        User owner = new User();
        owner.setId(2L);

        Item item = new Item();
        item.setId(1L);
        item.setAvailable(true);
        item.setOwner(owner);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStart(before);
        booking.setEnd(after);
        booking.setStatus(BookingStatus.WAITING);

        when(userRepository.findById(anyLong())).thenReturn(java.util.Optional.of(booker));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDto result = bookingService.addBooking(createDto, 1L);

        assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
        assertEquals(booking.getEnd(), result.getEnd());
        assertEquals(booking.getStart(), result.getStart());
        assertEquals(mapper.userToUserDto(booker), result.getBooker());
        assertEquals(mapper.itemToItemDto(item), result.getItem());
        assertEquals(booking.getStatus(), result.getStatus());

        verify(itemRepository, times(2)).findById(anyLong());
        verify(userRepository).findById(anyLong());
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void testAddBookingItemNotAvailable() {
        BookingCreateDto createDto = new BookingCreateDto();
        createDto.setItemId(1L);
        Long userId = 2L;

        Item item = new Item();
        item.setAvailable(false);
        when(itemRepository.findById(createDto.getItemId())).thenReturn(java.util.Optional.of(item));

        assertThrows(BadRequestException.class, () -> bookingService.addBooking(createDto, userId));

        verify(itemRepository, times(1)).findById(anyLong());
        verifyNoInteractions(userRepository, bookingRepository);
    }

    @Test
    void testAddBookingUserNotAvailable() {
        BookingCreateDto createDto = new BookingCreateDto();
        createDto.setItemId(1L);
        User owner = new User();
        owner.setId(1L);

        Item item = new Item();
        item.setAvailable(true);
        item.setOwner(owner);
        when(itemRepository.findById(createDto.getItemId())).thenReturn(java.util.Optional.of(item));
        when(userRepository.findById(createDto.getItemId())).thenReturn(java.util.Optional.of(owner));

        assertThrows(NotFoundException.class, () -> bookingService.addBooking(createDto, 1L));

        verify(itemRepository, times(2)).findById(anyLong());
        verify(userRepository, times(1)).findById(anyLong());
        verifyNoInteractions(bookingRepository);
    }

    @Test
    void testAddBookingItemNotFound() {
        BookingCreateDto createDto = new BookingCreateDto();
        createDto.setItemId(1L);
        User owner = new User();
        owner.setId(1L);

        Item item = new Item();
        item.setAvailable(true);
        item.setOwner(owner);
        when(itemRepository.findById(createDto.getItemId())).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> bookingService.addBooking(createDto, 1L));

        verify(itemRepository, times(1)).findById(anyLong());
        verifyNoInteractions(bookingRepository, userRepository);
    }

    @Test
    void testSetApprove_Success() {
        Long bookingId = 1L;
        Long ownerId = 1L;
        Long bookerId = 2L;
        User owner = new User(ownerId);
        User booker = new User(bookerId);
        Item item = new Item();
        item.setOwner(owner);

        Booking booking = new Booking(bookingId, LocalDateTime.now(), LocalDateTime.now().plusHours(1), item, booker, BookingStatus.WAITING);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(bookingRepository.save(booking)).thenReturn(booking);

        BookingDto result = bookingService.setApprove(ownerId, true, bookingId);

        assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
        assertEquals(BookingStatus.APPROVED, result.getStatus());

        verify(bookingRepository).findById(bookingId);
        verify(userRepository).findById(ownerId);
        verify(bookingRepository).save(booking);
    }

    @Test
    void testSetApproveNotFoundBooking() {
        Long bookingId = 1L;
        Long userId = 1L;
        when(bookingRepository.findById(anyLong())).thenThrow(new NotFoundException());
        assertThrows(NotFoundException.class, () -> bookingService.setApprove(userId, true, bookingId));
        verify(bookingRepository).findById(bookingId);
    }

    @Test
    void testSetApproveNotFoundUser() {
        Long bookingId = 1L;
        Long userId = 1L;
        Booking booking = new Booking(bookingId, LocalDateTime.now(), LocalDateTime.now().plusHours(1), new Item(), new User(userId), BookingStatus.WAITING);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(userRepository.findById(anyLong())).thenThrow(new NotFoundException());

        assertThrows(NotFoundException.class, () -> bookingService.setApprove(userId, true, bookingId));
        verify(userRepository).findById(bookingId);
    }

    @Test
    void testSetApproveCheckItemOwnerNotFound() {
        Long bookingId = 1L;
        Long ownerId = 1L;
        Long userId = 2L;
        Item item = new Item();
        User booker = new User(userId);
        User owner = new User(ownerId);
        item.setOwner(owner);

        Booking booking = new Booking(bookingId, LocalDateTime.now(), LocalDateTime.now().plusHours(1), item, booker, BookingStatus.WAITING);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));

        assertThrows(NotFoundException.class, () -> bookingService.setApprove(userId, true, bookingId));
        verify(userRepository).findById(userId);
        verify(bookingRepository).findById(bookingId);
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    void testSetApproveCheckItemOwnerBadRequest() {
        Long bookingId = 1L;
        Long ownerId = 1L;
        Long userId = 2L;
        Item item = new Item();
        User booker = new User(userId);
        User owner = new User(ownerId);
        item.setOwner(owner);

        Booking booking = new Booking(bookingId, LocalDateTime.now(), LocalDateTime.now().plusHours(1), item, booker, BookingStatus.WAITING);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User(3L)));

        assertThrows(BadRequestException.class, () -> bookingService.setApprove(3L, true, bookingId));
        verify(userRepository).findById(3L);
        verify(bookingRepository).findById(bookingId);
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    void testSetApproveCheckItemOwnerAlreadyApprovedExpectBadRequestException() {
        Long bookingId = 1L;
        Long ownerId = 1L;
        Long userId = 2L;
        Item item = new Item();
        User booker = new User(userId);
        User owner = new User(ownerId);
        item.setOwner(owner);

        Booking booking = new Booking(bookingId, LocalDateTime.now(), LocalDateTime.now().plusHours(1), item, booker, BookingStatus.APPROVED);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));

        assertThrows(BadRequestException.class, () -> bookingService.setApprove(ownerId, true, bookingId));
        verify(userRepository).findById(ownerId);
        verify(bookingRepository).findById(bookingId);
        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    void testSetApproveCheckItemOwnerNotApprovedExpectRejected() {
        Long bookingId = 1L;
        Long ownerId = 1L;
        Long userId = 2L;
        Item item = new Item();
        User booker = new User(userId);
        User owner = new User(ownerId);
        item.setOwner(owner);

        Booking booking = new Booking(bookingId, LocalDateTime.now(), LocalDateTime.now().plusHours(1), item, booker, BookingStatus.WAITING);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDto result = bookingService.setApprove(ownerId, false, bookingId);

        assertEquals(BookingStatus.REJECTED, result.getStatus());
        assertEquals(bookingId, result.getId());
        assertEquals(mapper.userToUserDto(booker), result.getBooker());
        assertEquals(mapper.itemToItemDto(item), result.getItem());

        verify(userRepository).findById(ownerId);
        verify(bookingRepository).save(booking);
        verify(bookingRepository).findById(bookingId);
    }

    @Test
    public void testGetByIdWithUserEqualsOwnerExpectBooking() {
        Long userId = 1L;
        Long bookerId = 2L;
        Long bookingId = 1L;
        User booker = new User(bookerId);
        User owner = new User(userId);
        Item item = new Item();
        item.setOwner(owner);
        Booking booking = new Booking();
        booking.setBooker(booker);
        booking.setItem(item);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        BookingDto result = bookingService.getById(1L, bookingId);

        assertNotNull(result);
        verify(bookingRepository).findById(bookingId);
    }

    @Test
    public void testGetByIdWithUserEqualsBookerExpectBooking() {
        Long userId = 1L;
        Long bookerId = 2L;
        Long bookingId = 1L;
        User booker = new User(bookerId);
        User owner = new User(userId);
        Item item = new Item();
        item.setOwner(owner);
        Booking booking = new Booking();
        booking.setBooker(booker);
        booking.setItem(item);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        BookingDto result = bookingService.getById(2L, bookingId);

        assertNotNull(result);
        verify(bookingRepository).findById(bookingId);
    }

    @Test
    public void testGetByIdWithUserEqualsBookerExpectNotFound() {
        Long userId = 1L;
        Long bookerId = 2L;
        Long bookingId = 1L;
        User booker = new User(bookerId);
        User owner = new User(userId);
        Item item = new Item();
        item.setOwner(owner);
        Booking booking = new Booking();
        booking.setBooker(booker);
        booking.setItem(item);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        assertThrows(NotFoundException.class, () -> bookingService.getById(3L, bookingId));

        verify(bookingRepository).findById(bookingId);
    }

    @Test
    public void getAllByUserIdStateAllCorrect() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime now2 = now.plusHours(1);
        Long userId = 1L;
        User user = new User(userId);
        Booking booking = new Booking();
        booking.setBooker(user);
        booking.setStart(now);
        Booking booking2 = new Booking();
        booking2.setBooker(user);
        booking2.setStart(now2);
        Pageable pageable = PageRequest.of(0, 2);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findBookingByBookerIdOrderByStartDesc(userId, pageable)).thenReturn(List.of(
                booking, booking2
        ));

        List<BookingDto> bookings = bookingService.getAllByUserId(userId, "ALL", 0L, 2L);

        assertNotNull(bookings);
        assertEquals(2, bookings.size());
        assertEquals(now, bookings.get(0).getStart());
        assertEquals(now2, bookings.get(1).getStart());
    }

    @Test
    public void getAllByUserIdStateFutureCorrect() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime now2 = now.plusHours(1);
        Long userId = 1L;
        User user = new User(userId);
        Booking booking = new Booking();
        booking.setBooker(user);
        booking.setStart(now);
        Booking booking2 = new Booking();
        booking2.setBooker(user);
        booking2.setStart(now2);
        Pageable pageable = PageRequest.of(0, 2);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findBookingByBookerIdAndStartAfterOrderByStartDesc(
                eq(userId),
                any(LocalDateTime.class),
                eq(pageable)
        )).thenReturn(List.of(
                booking2
        ));
        List<BookingDto> bookings = bookingService.getAllByUserId(userId, "FUTURE", 0L, 2L);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(now2, bookings.get(0).getStart());
    }

    @Test
    public void getAllByUserIdStateRejectedCorrect() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime now2 = now.plusHours(1);
        Long userId = 1L;
        User user = new User(userId);
        Booking booking = new Booking();
        booking.setBooker(user);
        booking.setStart(now);
        Booking booking2 = new Booking();
        booking2.setBooker(user);
        booking2.setStart(now2);
        Pageable pageable = PageRequest.of(0, 2);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findBookingByBookerIdAndStatusOrderByStartDesc(
                eq(userId),
                eq(BookingStatus.REJECTED),
                eq(pageable)
        )).thenReturn(List.of(
                booking2
        ));
        List<BookingDto> bookings = bookingService.getAllByUserId(userId, "REJECTED", 0L, 2L);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(now2, bookings.get(0).getStart());
    }

    @Test
    public void getAllByUserIdStateWaitingCorrect() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime now2 = now.plusHours(1);
        Long userId = 1L;
        User user = new User(userId);
        Booking booking = new Booking();
        booking.setBooker(user);
        booking.setStart(now);
        Booking booking2 = new Booking();
        booking2.setBooker(user);
        booking2.setStart(now2);
        Pageable pageable = PageRequest.of(0, 2);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findBookingByBookerIdAndStatusOrderByStartDesc(
                eq(userId),
                eq(BookingStatus.WAITING),
                eq(pageable)
        )).thenReturn(List.of(
                booking2
        ));
        List<BookingDto> bookings = bookingService.getAllByUserId(userId, "WAITING", 0L, 2L);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(now2, bookings.get(0).getStart());
    }

    @Test
    public void getAllByUserIdStateCurrentCorrect() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime now2 = now.plusHours(1);
        Long userId = 1L;
        User user = new User(userId);
        Booking booking = new Booking();
        booking.setBooker(user);
        booking.setStart(now);
        Booking booking2 = new Booking();
        booking2.setBooker(user);
        booking2.setStart(now2);
        Pageable pageable = PageRequest.of(0, 2);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findBookingByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
                eq(userId),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                eq(pageable)
        )).thenReturn(List.of(
                booking2
        ));
        List<BookingDto> bookings = bookingService.getAllByUserId(userId, "CURRENT", 0L, 2L);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(now2, bookings.get(0).getStart());
    }

    @Test
    public void getAllByUserIdStatePastCorrect() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime now2 = now.plusHours(1);
        Long userId = 1L;
        User user = new User(userId);
        Booking booking = new Booking();
        booking.setBooker(user);
        booking.setStart(now);
        Booking booking2 = new Booking();
        booking2.setBooker(user);
        booking2.setStart(now2);
        Pageable pageable = PageRequest.of(0, 2);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findBookingByBookerIdAndEndBeforeOrderByStartDesc(
                eq(userId),
                any(LocalDateTime.class),
                eq(pageable)
        )).thenReturn(List.of(
                booking2
        ));
        List<BookingDto> bookings = bookingService.getAllByUserId(userId, "PAST", 0L, 2L);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(now2, bookings.get(0).getStart());
    }

    @Test
    public void getAllByUserIdStateWrong() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime now2 = now.plusHours(1);
        Long userId = 1L;
        User user = new User(userId);
        Booking booking = new Booking();
        booking.setBooker(user);
        booking.setStart(now);
        Booking booking2 = new Booking();
        booking2.setBooker(user);
        booking2.setStart(now2);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        assertThrows(BadRequestException.class, () -> bookingService.getAllByUserId(userId, "WRONG", 0L, 2L));
    }

    @Test
    public void getAllByUserIdNegativeFrom() {
        assertThrows(BadRequestException.class, () -> bookingService.getAllByUserId(1L, "PAST", -20L, 2L));
    }

    @Test
    public void getAllByUserIdZeroSize() {
        assertThrows(BadRequestException.class, () -> bookingService.getAllByUserId(1L, "PAST", 0L, 0L));
    }

    @Test
    public void getAllByUserIdNegativeSize() {
        assertThrows(BadRequestException.class, () -> bookingService.getAllByUserId(1L, "PAST", 0L, -2L));
    }

    @Test
    public void getAllByOwnerIdStateAllCurrent() {
        Long ownerId = 1L;
        User owner = new User(ownerId);
        Item item = new Item();
        item.setId(1L);
        item.setOwner(owner);
        Item item2 = new Item();
        item2.setId(2L);
        item2.setOwner(owner);
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().minusMinutes(10));
        booking.setItem(item);
        Booking booking2 = new Booking();
        booking2.setItem(item2);
        booking2.setStart(LocalDateTime.now().minusMinutes(30));

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(itemRepository.findItemsByOwnerIdOrderById(ownerId)).thenReturn(List.of(item, item2));
        when(bookingRepository.findAll()).thenReturn(List.of(booking, booking2));

        List<BookingDto> bookings = bookingService.getAllByOwnerId(ownerId, "ALL", 0L, 2L);

        assertNotNull(bookings);
        assertEquals(2, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
        assertEquals(booking2.getId(), bookings.get(1).getId());
    }

    @Test
    public void getAllByOwnerIdStateFutureCurrent() {
        Long ownerId = 1L;
        User owner = new User(ownerId);
        Item item = new Item();
        item.setId(1L);
        item.setOwner(owner);
        Item item2 = new Item();
        item2.setId(2L);
        item2.setOwner(owner);
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().plusMinutes(10));
        booking.setItem(item);
        Booking booking2 = new Booking();
        booking2.setItem(item2);
        booking2.setStart(LocalDateTime.now().plusMinutes(30));

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(itemRepository.findItemsByOwnerIdOrderById(ownerId)).thenReturn(List.of(item, item2));
        when(bookingRepository.findAll()).thenReturn(List.of(booking, booking2));

        List<BookingDto> bookings = bookingService.getAllByOwnerId(ownerId, "FUTURE", 0L, 2L);

        assertNotNull(bookings);
        assertEquals(2, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
        assertEquals(booking2.getId(), bookings.get(1).getId());
    }

    @Test
    public void getAllByOwnerIdStateRejectedCurrent() {
        Long ownerId = 1L;
        User owner = new User(ownerId);
        Item item = new Item();
        item.setId(1L);
        item.setOwner(owner);
        Item item2 = new Item();
        item2.setId(2L);
        item2.setOwner(owner);
        Booking booking = new Booking();
        booking.setStatus(BookingStatus.REJECTED);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now().plusMinutes(10));
        Booking booking2 = new Booking();
        booking2.setItem(item2);
        booking2.setStatus(BookingStatus.REJECTED);
        booking2.setStart(LocalDateTime.now().plusMinutes(30));

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(itemRepository.findItemsByOwnerIdOrderById(ownerId)).thenReturn(List.of(item, item2));
        when(bookingRepository.findAll()).thenReturn(List.of(booking, booking2));

        List<BookingDto> bookings = bookingService.getAllByOwnerId(ownerId, "REJECTED", 0L, 2L);

        assertNotNull(bookings);
        assertEquals(2, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
        assertEquals(booking2.getId(), bookings.get(1).getId());
    }

    @Test
    public void getAllByOwnerIdStateWaitingCurrent() {
        Long ownerId = 1L;
        User owner = new User(ownerId);
        Item item = new Item();
        item.setId(1L);
        item.setOwner(owner);
        Item item2 = new Item();
        item2.setId(2L);
        item2.setOwner(owner);
        Booking booking = new Booking();
        booking.setStatus(BookingStatus.WAITING);
        booking.setItem(item);
        booking.setStart(LocalDateTime.now().plusMinutes(10));
        Booking booking2 = new Booking();
        booking2.setItem(item2);
        booking2.setStatus(BookingStatus.WAITING);
        booking2.setStart(LocalDateTime.now().plusMinutes(30));

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(itemRepository.findItemsByOwnerIdOrderById(ownerId)).thenReturn(List.of(item, item2));
        when(bookingRepository.findAll()).thenReturn(List.of(booking, booking2));

        List<BookingDto> bookings = bookingService.getAllByOwnerId(ownerId, "WAITING", 0L, 2L);

        assertNotNull(bookings);
        assertEquals(2, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
        assertEquals(booking2.getId(), bookings.get(1).getId());
    }

    @Test
    public void getAllByOwnerIdStateCurrent() {
        Long ownerId = 1L;
        User owner = new User(ownerId);
        Item item = new Item();
        item.setId(1L);
        item.setOwner(owner);
        Item item2 = new Item();
        item2.setId(2L);
        item2.setOwner(owner);
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setEnd(LocalDateTime.now().plusMinutes(10));
        booking.setStart(LocalDateTime.now().minusMinutes(10));
        Booking booking2 = new Booking();
        booking2.setItem(item2);
        booking2.setEnd(LocalDateTime.now().plusMinutes(30));
        booking2.setStart(LocalDateTime.now().minusMinutes(30));

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(itemRepository.findItemsByOwnerIdOrderById(ownerId)).thenReturn(List.of(item, item2));
        when(bookingRepository.findAll()).thenReturn(List.of(booking, booking2));

        List<BookingDto> bookings = bookingService.getAllByOwnerId(ownerId, "CURRENT", 0L, 2L);

        assertNotNull(bookings);
        assertEquals(2, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
        assertEquals(booking2.getId(), bookings.get(1).getId());
    }

    @Test
    public void getAllByOwnerIdStatePastCurrent() {
        Long ownerId = 1L;
        User owner = new User(ownerId);
        Item item = new Item();
        item.setId(1L);
        item.setOwner(owner);
        Item item2 = new Item();
        item2.setId(2L);
        item2.setOwner(owner);
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setEnd(LocalDateTime.now().minusMinutes(30));
        booking.setStart(LocalDateTime.now().plusMinutes(10));
        Booking booking2 = new Booking();
        booking2.setItem(item2);
        booking2.setEnd(LocalDateTime.now().minusMinutes(60));
        booking2.setStart(LocalDateTime.now().plusMinutes(30));

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(itemRepository.findItemsByOwnerIdOrderById(ownerId)).thenReturn(List.of(item, item2));
        when(bookingRepository.findAll()).thenReturn(List.of(booking, booking2));

        List<BookingDto> bookings = bookingService.getAllByOwnerId(ownerId, "PAST", 0L, 2L);

        assertNotNull(bookings);
        assertEquals(2, bookings.size());
        assertEquals(booking.getId(), bookings.get(0).getId());
        assertEquals(booking2.getId(), bookings.get(1).getId());
    }

    @Test
    public void getAllByOwnerIdStateWrong() {
        Long ownerId = 1L;
        User owner = new User(ownerId);
        Item item = new Item();
        item.setId(1L);
        item.setOwner(owner);
        Item item2 = new Item();
        item2.setId(2L);
        item2.setOwner(owner);
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setEnd(LocalDateTime.now().minusMinutes(30));
        booking.setStart(LocalDateTime.now().plusMinutes(10));
        Booking booking2 = new Booking();
        booking2.setItem(item2);
        booking2.setEnd(LocalDateTime.now().minusMinutes(60));
        booking2.setStart(LocalDateTime.now().plusMinutes(30));

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(itemRepository.findItemsByOwnerIdOrderById(ownerId)).thenReturn(List.of(item, item2));
        when(bookingRepository.findAll()).thenReturn(List.of(booking, booking2));

        assertThrows(BadRequestException.class, () -> bookingService.getAllByOwnerId(ownerId, "WRONGSTATE", 0L, 2L));
    }

    @Test
    public void getAllByOwnerIdNegativeFrom() {
        assertThrows(BadRequestException.class, () -> bookingService.getAllByOwnerId(1L, "PAST", -20L, 2L));
    }

    @Test
    public void getAllByOwnerIdZeroSize() {
        assertThrows(BadRequestException.class, () -> bookingService.getAllByOwnerId(1L, "PAST", 0L, 0L));
    }

    @Test
    public void getAllByOwnerIdNegativeSize() {
        assertThrows(BadRequestException.class, () -> bookingService.getAllByOwnerId(1L, "PAST", 0L, -2L));
    }

}