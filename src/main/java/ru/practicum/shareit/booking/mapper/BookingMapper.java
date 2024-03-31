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
public class BookingMapper {

//    Booking toBooking(BookingCreateDto bookingDto);
//
//    BookingDto toDto(Booking booking);


    public Booking toBooking(BookingCreateDto bookingDto) {
        if (bookingDto == null) {
            return null;
        } else {
            Booking booking = new Booking();
            booking.setId(bookingDto.getId());
            booking.setStart(bookingDto.getStart());
            booking.setEnd(bookingDto.getEnd());
            return booking;
        }
    }

    public BookingDto toDto(Booking booking) {
        if (booking == null) {
            return null;
        } else {
            BookingDto bookingDto = new BookingDto();
            bookingDto.setId(booking.getId());
            bookingDto.setStart(booking.getStart());
            bookingDto.setEnd(booking.getEnd());
            bookingDto.setStatus(booking.getStatus());
            bookingDto.setBooker(this.userToUserDto(booking.getBooker()));
            bookingDto.setItem(this.itemToItemDto(booking.getItem()));
            return bookingDto;
        }
    }

    public UserDto userToUserDto(User user) {
        if (user == null) {
            return null;
        } else {
            UserDto userDto = new UserDto();
            userDto.setId(user.getId());
            userDto.setName(user.getName());
            userDto.setEmail(user.getEmail());
            return userDto;
        }
    }

    public ItemDto itemToItemDto(Item item) {
        if (item == null) {
            return null;
        } else {
            ItemDto itemDto = new ItemDto();
            itemDto.setId(item.getId());
            itemDto.setName(item.getName());
            itemDto.setDescription(item.getDescription());
            itemDto.setAvailable(item.getAvailable());
            return itemDto;
        }
    }
}
