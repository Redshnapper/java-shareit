package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.BookingView;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserView;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    List<BookingStatus> wrongStatuses = List.of(BookingStatus.WAITING, BookingStatus.REJECTED, BookingStatus.CANCELED);
    LocalDateTime now = LocalDateTime.now();

    @Test
    @DirtiesContext
    void findBookingByBookerIdOrderByStartDesc() {
        LocalDateTime now2 = now.plusHours(1);
        User user = new User();
        userRepository.save(user);
        Pageable pageable = PageRequest.of(0, 2);
        Booking booking = new Booking();
        booking.setBooker(user);
        booking.setStart(now);
        bookingRepository.save(booking);
        Booking booking2 = new Booking();
        booking2.setBooker(user);
        booking2.setStart(now2);
        bookingRepository.save(booking2);

        List<Booking> bookings = bookingRepository.findBookingByBookerIdOrderByStartDesc(user.getId(), pageable);

        assertEquals(2, bookings.size());
        assertEquals(booking2, bookings.get(0));
        assertEquals(booking, bookings.get(1));
    }

    @Test
    @DirtiesContext
    void findBookingByBookerWrongId() {
        LocalDateTime now2 = now.plusHours(1);
        User user = new User();
        userRepository.save(user);
        Pageable pageable = PageRequest.of(0, 2);
        Booking booking = new Booking();
        booking.setBooker(user);
        booking.setStart(now);
        bookingRepository.save(booking);
        Booking booking2 = new Booking();
        booking2.setBooker(user);
        booking2.setStart(now2);
        bookingRepository.save(booking2);

        List<Booking> bookings = bookingRepository.findBookingByBookerIdOrderByStartDesc(3L, pageable);
        assertEquals(0, bookings.size());
        assertNotNull(bookings);

        bookings = bookingRepository.findBookingByBookerIdOrderByStartDesc(-13L, pageable);
        assertEquals(0, bookings.size());
        assertNotNull(bookings);

        bookings = bookingRepository.findBookingByBookerIdOrderByStartDesc(0L, pageable);
        assertEquals(0, bookings.size());
        assertNotNull(bookings);
    }

    @Test
    @DirtiesContext
    void findBookingByBookerIdEmpty() {
        Pageable pageable = PageRequest.of(0, 2);
        List<Booking> bookings = bookingRepository.findBookingByBookerIdOrderByStartDesc(1L, pageable);

        assertEquals(0, bookings.size());
        assertNotNull(bookings);
    }

    @Test
    @DirtiesContext
    void findBookingByBookerIdAndStartAfterOrderByStartDesc() {
        LocalDateTime now2 = now.plusHours(2);
        LocalDateTime now3 = now.plusHours(3);

        User user = new User();
        userRepository.save(user);
        Pageable pageable = PageRequest.of(0, 2);
        Booking booking = new Booking();
        booking.setBooker(user);
        booking.setStart(now2);
        bookingRepository.save(booking);
        Booking booking2 = new Booking();
        booking2.setBooker(user);
        booking2.setStart(now3);
        bookingRepository.save(booking2);
        List<Booking> bookings = bookingRepository.findBookingByBookerIdAndStartAfterOrderByStartDesc(user.getId(), now, pageable);

        assertEquals(2, bookings.size());
        assertEquals(booking2, bookings.get(0));
        assertEquals(booking, bookings.get(1));

        booking.setStart(now.minusHours(1));
        booking2.setStart(now.minusHours(2));
        bookings = bookingRepository.findBookingByBookerIdAndStartAfterOrderByStartDesc(user.getId(), now, pageable);

        assertEquals(0, bookings.size());
        assertNotNull(bookings);
    }

    @Test
    @DirtiesContext
    void findBookingByBookerStartAfterWithWrongId() {
        LocalDateTime now2 = now.plusHours(1);

        User user = new User();
        userRepository.save(user);
        Pageable pageable = PageRequest.of(0, 2);
        Booking booking = new Booking();
        booking.setBooker(user);
        booking.setStart(now);
        bookingRepository.save(booking);
        Booking booking2 = new Booking();
        booking2.setBooker(user);
        booking2.setStart(now2);
        bookingRepository.save(booking2);

        List<Booking> bookings = bookingRepository.findBookingByBookerIdAndStartAfterOrderByStartDesc(3L, LocalDateTime.now(), pageable);
        assertEquals(0, bookings.size());
        assertNotNull(bookings);

        bookings = bookingRepository.findBookingByBookerIdAndStartAfterOrderByStartDesc(-13L, LocalDateTime.now(), pageable);
        assertEquals(0, bookings.size());
        assertNotNull(bookings);

        bookings = bookingRepository.findBookingByBookerIdAndStartAfterOrderByStartDesc(0L, LocalDateTime.now(), pageable);
        assertEquals(0, bookings.size());
        assertNotNull(bookings);
    }

    @Test
    @DirtiesContext
    void findBookingByBookerIdStartAfterReturnEmpty() {
        Pageable pageable = PageRequest.of(0, 2);
        List<Booking> bookings = bookingRepository.findBookingByBookerIdAndStartAfterOrderByStartDesc(1L, LocalDateTime.now(), pageable);

        assertEquals(0, bookings.size());
        assertNotNull(bookings);
    }


    @Test
    @DirtiesContext
    void findBookingByBookerIdAndStatus() {
        User user = new User();
        userRepository.save(user);
        Pageable pageable = PageRequest.of(0, 2);
        Booking booking = new Booking();
        booking.setBooker(user);
        booking.setStatus(BookingStatus.REJECTED);
        bookingRepository.save(booking);
        Booking booking2 = new Booking();
        booking2.setBooker(user);
        booking2.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking2);
        List<Booking> bookings = bookingRepository.findBookingByBookerIdAndStatusOrderByStartDesc(user.getId(), BookingStatus.REJECTED, pageable);

        assertEquals(1, bookings.size());
        assertEquals(booking, bookings.get(0));

        bookings = bookingRepository.findBookingByBookerIdAndStatusOrderByStartDesc(user.getId(), BookingStatus.APPROVED, pageable);

        assertEquals(1, bookings.size());
        assertEquals(booking2, bookings.get(0));

        booking.setStatus(BookingStatus.WAITING);
        bookings = bookingRepository.findBookingByBookerIdAndStatusOrderByStartDesc(user.getId(), BookingStatus.WAITING, pageable);
        assertEquals(1, bookings.size());
        assertEquals(booking, bookings.get(0));

        booking.setStatus(BookingStatus.CANCELED);
        bookings = bookingRepository.findBookingByBookerIdAndStatusOrderByStartDesc(user.getId(), BookingStatus.CANCELED, pageable);
        assertEquals(1, bookings.size());
        assertEquals(booking, bookings.get(0));
    }

    @Test
    @DirtiesContext
    void findBookingByBookerAndStatusReturnEmpty() {
        Pageable pageable = PageRequest.of(0, 2);
        List<Booking> bookings = bookingRepository.findBookingByBookerIdAndStatusOrderByStartDesc(1L, BookingStatus.CANCELED, pageable);

        assertEquals(0, bookings.size());
        assertNotNull(bookings);
    }

    @Test
    @DirtiesContext
    void findBookingByBookerIdAndStartIsBeforeAndEndIsAfter() {
        LocalDateTime before = now.minusHours(2);
        LocalDateTime after = now.plusHours(2);

        User user = new User();
        userRepository.save(user);
        Pageable pageable = PageRequest.of(0, 1);
        Booking booking = new Booking();
        booking.setBooker(user);
        booking.setStart(before);
        booking.setEnd(after);
        bookingRepository.save(booking);

        Booking booking2 = new Booking();
        booking.setBooker(user);
        booking.setStart(before);
        booking.setEnd(after);
        bookingRepository.save(booking2);

        List<Booking> bookings = bookingRepository.findBookingByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(user.getId(), now, now, pageable);

        assertEquals(1, bookings.size());
        assertEquals(booking, bookings.get(0));
    }

    @Test
    @DirtiesContext
    void findBookingByBookerIdEmptyWithStartAndEnd() {
        LocalDateTime before = now.minusHours(2);
        LocalDateTime after = now.plusHours(2);

        User user = new User();
        userRepository.save(user);
        Pageable pageable = PageRequest.of(0, 1);
        Booking booking = new Booking();
        booking.setBooker(user);
        booking.setStart(before);
        booking.setEnd(before.plusMinutes(10));
        bookingRepository.save(booking);

        Booking booking2 = new Booking();
        booking.setBooker(user);
        booking.setStart(after);
        booking.setEnd(after.plusMinutes(10));
        bookingRepository.save(booking2);

        List<Booking> bookings = bookingRepository.findBookingByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(user.getId(), now, now, pageable);

        assertEquals(0, bookings.size());
        assertNotNull(bookings);
    }

    @Test
    @DirtiesContext
    void findBookingByBookerIdEmptyWithNullStartAndEnd() {
        User user = new User();
        userRepository.save(user);
        Pageable pageable = PageRequest.of(0, 3);
        Booking booking = new Booking();
        booking.setBooker(user);
        bookingRepository.save(booking);

        Booking booking2 = new Booking();
        booking2.setBooker(user);
        bookingRepository.save(booking2);

        List<Booking> bookings = bookingRepository.findBookingByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(user.getId(), now, now, pageable);

        assertEquals(user, booking.getBooker());
        assertEquals(user, booking2.getBooker());
        assertEquals(0, bookings.size());
        assertNotNull(bookings);
    }

    @Test
    @DirtiesContext
    void findBookingByBookerIdAndEndBeforeOrderByStartDesc() {
        LocalDateTime before = now.minusHours(2);

        User user = new User();
        userRepository.save(user);
        Pageable pageable = PageRequest.of(0, 2);
        Booking booking = new Booking();
        booking.setBooker(user);
        booking.setStart(before);
        booking.setEnd(before.plusMinutes(10));
        bookingRepository.save(booking);

        Booking booking2 = new Booking();
        booking2.setBooker(user);
        booking2.setStart(before.plusMinutes(30));
        booking2.setEnd(before.plusMinutes(10));
        bookingRepository.save(booking2);

        List<Booking> bookings = bookingRepository.findBookingByBookerIdAndEndBeforeOrderByStartDesc(user.getId(), now, pageable);

        assertEquals(2, bookings.size());
        assertEquals(booking2, bookings.get(0));
        assertEquals(booking, bookings.get(1));
    }

    @Test
    @DirtiesContext
    void findBookingByBookerIdAndNullEnd() {
        LocalDateTime before = now.minusHours(2);
        User user = new User();
        userRepository.save(user);
        Pageable pageable = PageRequest.of(0, 2);
        Booking booking = new Booking();
        booking.setBooker(user);
        booking.setStart(before);
        booking.setEnd(null);
        bookingRepository.save(booking);

        Booking booking2 = new Booking();
        booking2.setBooker(user);
        booking2.setStart(before.plusMinutes(30));
        booking2.setEnd(before.plusMinutes(10));
        bookingRepository.save(booking2);

        List<Booking> bookings = bookingRepository.findBookingByBookerIdAndEndBeforeOrderByStartDesc(user.getId(), now, pageable);

        assertEquals(1, bookings.size());
        assertEquals(booking2, bookings.get(0));
    }

    @Test
    @DirtiesContext
    void findBookingByItemIdAndBookerIdAndStatusNotInAndEndBefore() {
        LocalDateTime before = now.minusHours(2);
        List<BookingStatus> wrongStatuses = List.of(BookingStatus.WAITING, BookingStatus.REJECTED, BookingStatus.CANCELED);
        Item item = new Item();
        itemRepository.save(item);
        User user = new User();
        userRepository.save(user);

        Booking booking = new Booking();
        booking.setBooker(user);
        booking.setItem(item);
        booking.setEnd(before);
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);

        Booking booking2 = new Booking();
        booking2.setBooker(user);
        booking2.setItem(item);
        booking.setEnd(before);
        booking2.setStatus(BookingStatus.WAITING);
        bookingRepository.save(booking2);

        List<Booking> bookings = bookingRepository.findBookingByItemIdAndBookerIdAndStatusNotInAndEndBefore(item.getId(),
                user.getId(), wrongStatuses, now);

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(booking, bookings.get(0));
    }

    @Test
    @DirtiesContext
    void findBookingByItemIdAndBookerIdAndStatusNotInAndNullNow() {
        LocalDateTime before = now.minusHours(2);
        Item item = new Item();
        itemRepository.save(item);
        User user = new User();
        userRepository.save(user);

        Booking booking = new Booking();
        booking.setBooker(user);
        booking.setItem(item);
        booking.setEnd(before);
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);

        Booking booking2 = new Booking();
        booking2.setBooker(user);
        booking2.setItem(item);
        booking.setEnd(null);
        booking2.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking2);

        List<Booking> bookings = bookingRepository.findBookingByItemIdAndBookerIdAndStatusNotInAndEndBefore(item.getId(),
                user.getId(), wrongStatuses, null);

        assertNotNull(bookings);
        assertEquals(0, bookings.size());
    }


    @Test
    @DirtiesContext
    void findByItemIdAndEndIsBeforeAndStatusNotInOrderByEndDesc() {
        LocalDateTime before = now.minusHours(2);
        Item item = new Item();
        itemRepository.save(item);
        User user = new User();
        userRepository.save(user);

        Booking booking = new Booking();
        booking.setBooker(user);
        booking.setItem(item);
        booking.setEnd(before);
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);

        Booking booking2 = new Booking();
        booking2.setBooker(user);
        booking2.setItem(item);
        booking2.setEnd(before);
        booking2.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking2);

        BookingView view = new BookingView() {
            @Override
            public Long getId() {
                return 1L;
            }

            @Override
            public UserView getBooker() {
                return () -> 1L;
            }
        };
        BookingView view2 = new BookingView() {
            @Override
            public Long getId() {
                return 2L;
            }

            @Override
            public UserView getBooker() {
                return () -> 1L;
            }
        };

        List<BookingView> bookingViews = bookingRepository.findByItemIdAndEndIsBeforeAndStatusNotInOrderByEndDesc(item.getId(),
                now, wrongStatuses);

        assertNotNull(bookingViews);
        assertEquals(2, bookingViews.size());
        BookingView savedView = bookingViews.get(0);
        BookingView savedView2 = bookingViews.get(1);

        assertEquals(view.getId(), savedView.getId());
        assertEquals(view2.getId(), savedView2.getId());

        assertEquals(view.getBooker().getId(), savedView.getBooker().getId());
        assertEquals(view2.getBooker().getId(), savedView2.getBooker().getId());
    }

    @Test
    @DirtiesContext
    void findByItemIdAndStartIsAfterAndStatusNotInOrderByStart() {
        LocalDateTime after = now.plusHours(2);
        Item item = new Item();
        itemRepository.save(item);
        User user = new User();
        userRepository.save(user);

        Booking booking = new Booking();
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStart(after);
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);

        Booking booking2 = new Booking();
        booking2.setBooker(user);
        booking2.setItem(item);
        booking2.setStart(after);
        booking2.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking2);

        BookingView view = new BookingView() {
            @Override
            public Long getId() {
                return 1L;
            }

            @Override
            public UserView getBooker() {
                return () -> 1L;
            }
        };
        BookingView view2 = new BookingView() {
            @Override
            public Long getId() {
                return 2L;
            }

            @Override
            public UserView getBooker() {
                return () -> 1L;
            }
        };

        List<BookingView> bookingViews = bookingRepository.findByItemIdAndStartIsAfterAndStatusNotInOrderByStart(item.getId(),
                now, wrongStatuses);

        assertNotNull(bookingViews);
        assertEquals(2, bookingViews.size());
        BookingView savedView = bookingViews.get(0);
        BookingView savedView2 = bookingViews.get(1);

        assertEquals(view.getId(), savedView.getId());
        assertEquals(view2.getId(), savedView2.getId());

        assertEquals(view.getBooker().getId(), savedView.getBooker().getId());
        assertEquals(view2.getBooker().getId(), savedView2.getBooker().getId());
    }

    @Test
    @DirtiesContext
    void findByItemIdAndStartIsBeforeAndEndIsAfterAndStatusNotInOrderByEndDesc() {
        LocalDateTime before = now.minusHours(2);
        LocalDateTime after = now.plusHours(2);
        Item item = new Item();
        itemRepository.save(item);
        User user = new User();
        userRepository.save(user);

        Booking booking = new Booking();
        booking.setBooker(user);
        booking.setItem(item);
        booking.setEnd(after);
        booking.setStart(before);
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);

        Booking booking2 = new Booking();
        booking2.setBooker(user);
        booking2.setItem(item);
        booking2.setStart(after);
        booking2.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking2);

        BookingView view = new BookingView() {
            @Override
            public Long getId() {
                return 1L;
            }

            @Override
            public UserView getBooker() {
                return () -> 1L;
            }
        };

        List<BookingView> bookingViews = bookingRepository.findByItemIdAndStartIsBeforeAndEndIsAfterAndStatusNotInOrderByEndDesc(item.getId(),
                now, now, wrongStatuses);

        assertNotNull(bookingViews);
        assertEquals(1, bookingViews.size());
        BookingView savedView = bookingViews.get(0);
        assertEquals(view.getId(), savedView.getId());
        assertEquals(view.getBooker().getId(), savedView.getBooker().getId());
    }
}