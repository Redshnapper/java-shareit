package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.BookingView;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findBookingByBookerIdOrderByStartDesc(Long id, Pageable pageable);

    List<Booking> findBookingByBookerIdAndStartAfterOrderByStartDesc(Long id, LocalDateTime now, Pageable pageable);

    List<Booking> findBookingByBookerIdAndStatusOrderByStartDesc(Long id, BookingStatus status, Pageable pageable);

    List<Booking> findBookingByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Long id,
                                                                                     LocalDateTime start,
                                                                                     LocalDateTime end, Pageable pageable);

    List<Booking> findBookingByBookerIdAndEndBeforeOrderByStartDesc(Long id, LocalDateTime now, Pageable pageable);

    List<Booking> findBookingByItemIdAndBookerIdAndStatusNotInAndEndBefore(Long itemId,
                                                                           Long userId,
                                                                           List<BookingStatus> statuses,
                                                                           LocalDateTime now);

    List<BookingView> findByItemIdAndEndIsBeforeAndStatusNotInOrderByEndDesc(Long itemId,
                                                                             LocalDateTime now,
                                                                             List<BookingStatus> statuses); // last

    List<BookingView> findByItemIdAndStartIsAfterAndStatusNotInOrderByStart(Long itemId,
                                                                            LocalDateTime now,
                                                                            List<BookingStatus> statuses); // next

    List<BookingView> findByItemIdAndStartIsBeforeAndEndIsAfterAndStatusNotInOrderByEndDesc(Long itemId,
                                                                                            LocalDateTime now,
                                                                                            LocalDateTime now1,
                                                                                            List<BookingStatus> statuses);

}
