package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingAddRequest;
import ru.practicum.shareit.booking.model.StateParam;

import java.util.List;

public interface BookingService {

    BookingDto addBooking(Long userId, NewBookingAddRequest newBookingAddRequest);

    BookingDto approvedBooking(Long userId, Long bookingId, Boolean approved);

    BookingDto getBookingByBookerIdOrOwnerId(Long userId, Long bookingId);

    List<BookingDto> getUserBookings(Long userId, StateParam state);

    List<BookingDto> getBookingsForItemsByOwnerId(Long userId, StateParam state);

}