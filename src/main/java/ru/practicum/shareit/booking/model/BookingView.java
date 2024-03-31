package ru.practicum.shareit.booking.model;

import ru.practicum.shareit.user.model.UserView;


public interface BookingView {

    Long getId();

    UserView getBooker();
}
