package ru.practicum.shareit.valitator;

import ru.practicum.shareit.booking.dto.BookingCreateDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class BookerDateValidator implements ConstraintValidator<BookingDate, BookingCreateDto> {
    @Override
    public void initialize(BookingDate constraint) {
    }

    @Override
    public boolean isValid(BookingCreateDto bookingCreateDto, ConstraintValidatorContext constraintValidatorContext) {
        if (bookingCreateDto.getStart() == null || bookingCreateDto.getEnd() == null) {
            return false;
        }
        return !bookingCreateDto.getStart().isEqual(bookingCreateDto.getEnd())
                && !bookingCreateDto.getEnd().isBefore(bookingCreateDto.getStart());
    }
}
