package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.valitator.BookingDate;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@BookingDate
public class BookingCreateDto {
    private Long id;

    @FutureOrPresent
    @Future
    private LocalDateTime start;

    @FutureOrPresent
    @Future
    private LocalDateTime end;

    @NotNull
    private Long itemId;
}






