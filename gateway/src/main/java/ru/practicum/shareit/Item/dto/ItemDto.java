package ru.practicum.shareit.Item.dto;

import lombok.*;
import ru.practicum.shareit.booking.dto.BookingItemDto;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingItemDto lastBooking;
    private BookingItemDto nextBooking;
    private List<CommentDto> comments = new ArrayList<>();
}
