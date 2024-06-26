package ru.practicum.shareit.booking.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BookingCreateDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId;
}






