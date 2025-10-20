package ru.practicum.shareit.booking.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.dto.NewBookingAddRequest;
import ru.practicum.shareit.booking.dto.StateParam;
import ru.practicum.shareit.util.AppValidation;

import static ru.practicum.shareit.util.AppConstant.CUSTOM_REQUEST_HEADER_USER_ID;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> addBooking(@RequestHeader(CUSTOM_REQUEST_HEADER_USER_ID) Long userId,
                                             @RequestBody NewBookingAddRequest newBookingAddRequest) {
        AppValidation.bookingValidator(newBookingAddRequest);
        log.info("BookingController: Создание бронирования: пользовательId={}, запрос={}", userId, newBookingAddRequest);
        return bookingClient.addBooking(userId, newBookingAddRequest);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approvedBooking(@RequestHeader(CUSTOM_REQUEST_HEADER_USER_ID) Long userId,
                                                  @PathVariable(name = "bookingId") Long bookingId,
                                                  @RequestParam(name = "approved") Boolean approved) {
        log.info("BookingController: Подтверждение бронирования: пользовательId={}, bookingId={}, approved={}", userId, bookingId, approved);
        return bookingClient.approvedBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingByBookerIdOrOwnerId(@RequestHeader(CUSTOM_REQUEST_HEADER_USER_ID) Long userId,
                                                                @PathVariable(name = "bookingId") Long bookingId) {
        log.info("BookingController: Получение бронирования: пользовательId={}, bookingId={}", userId, bookingId);
        return bookingClient.getBookingByBookerIdOrOwnerId(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserBookings(@RequestHeader(CUSTOM_REQUEST_HEADER_USER_ID) Long userId,
                                                  @RequestParam(name = "state", defaultValue = "ALL") String state) {
        log.info("BookingController: Получение списка бронирований пользователя: пользовательId={}, state={}", userId, state);
        return bookingClient.getUserBookings(userId, StateParam.fromString(state));
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsForItemsByOwnerId(@RequestHeader(CUSTOM_REQUEST_HEADER_USER_ID) Long userId,
                                                               @RequestParam(name = "state", defaultValue = "ALL") StateParam state) {
        log.info("BookingController: Получение бронирований для вещей владельца: владелецId={}, state={}", userId, state);
        return bookingClient.getBookingsForItemsByOwnerId(userId, state);
    }

}