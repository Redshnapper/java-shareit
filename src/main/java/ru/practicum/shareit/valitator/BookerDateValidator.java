package ru.practicum.shareit.valitator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class BookerDateValidator implements ConstraintValidator<BookingDate, LocalDateTime> {
    LocalDateTime now = LocalDateTime.now();

    @Override
    public void initialize(BookingDate constraint) {
    }

    @Override
    public boolean isValid(LocalDateTime value, ConstraintValidatorContext context) {
        return !value.isBefore(now);
    }
}
