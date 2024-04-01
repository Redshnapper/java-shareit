package ru.practicum.shareit.booking.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@Component
public interface BookingMapper {
    Booking toBooking(BookingCreateDto bookingDto);

    BookingDto toDto(Booking booking);

    UserDto userToUserDto(User user);

    ItemDto itemToItemDto(Item item);
}
