package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingAddRequest;
import ru.practicum.shareit.booking.model.StateParam;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

import static ru.practicum.shareit.util.AppConstant.CUSTOM_REQUEST_HEADER_USER_ID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingDto> addBooking(@RequestHeader(CUSTOM_REQUEST_HEADER_USER_ID) Long userId,
                                                 @RequestBody NewBookingAddRequest newBookingAddRequest) {
        log.info("BookingController: Создание бронирования: пользовательId={}, запрос={}", userId, newBookingAddRequest);
        BookingDto bookingDto = bookingService.addBooking(userId, newBookingAddRequest);
        log.info("BookingController: Бронирование создано: bookingId={}", bookingDto.getId());
        return ResponseEntity.ok(bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> approvedBooking(@RequestHeader(CUSTOM_REQUEST_HEADER_USER_ID) Long userId,
                                                      @PathVariable(name = "bookingId") Long bookingId,
                                                      @RequestParam(name = "approved") Boolean approved) {
        log.info("BookingController: Подтверждение бронирования: пользовательId={}, bookingId={}, approved={}", userId, bookingId, approved);
        BookingDto bookingDto = bookingService.approvedBooking(userId, bookingId, approved);
        log.info("BookingController: Бронирование обновлено: bookingId={}, статус={}", bookingDto.getId(), bookingDto.getStatus());
        return ResponseEntity.ok(bookingDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> getBookingByBookerIdOrOwnerId(@RequestHeader(CUSTOM_REQUEST_HEADER_USER_ID) Long userId,
                                                                    @PathVariable(name = "bookingId") Long bookingId) {
        log.info("BookingController: Получение бронирования: пользовательId={}, bookingId={}", userId, bookingId);
        BookingDto bookingDto = bookingService.getBookingByBookerIdOrOwnerId(userId, bookingId);
        log.info("BookingController: Бронирование найдено: bookingId={}", bookingDto.getId());
        return ResponseEntity.ok(bookingDto);
    }

    @GetMapping
    public ResponseEntity<List<BookingDto>> getUserBookings(@RequestHeader(CUSTOM_REQUEST_HEADER_USER_ID) Long userId,
                                                            @RequestParam(name = "state", defaultValue = "ALL") String state) {
        log.info("BookingController: Получение списка бронирований пользователя: пользовательId={}, state={}", userId, state);
        List<BookingDto> bookingsDto = bookingService.getUserBookings(userId, StateParam.fromString(state));
        log.info("BookingController: Найдено бронирований: {}", bookingsDto.size());
        return ResponseEntity.ok(bookingsDto);
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingDto>> getBookingsForItemsByOwnerId(@RequestHeader(CUSTOM_REQUEST_HEADER_USER_ID) Long userId,
                                                                         @RequestParam(name = "state", defaultValue = "ALL") StateParam state) {
        log.info("BookingController: Получение бронирований для вещей владельца: владелецId={}, state={}", userId, state);
        List<BookingDto> bookingsDto = bookingService.getBookingsForItemsByOwnerId(userId, state);
        log.info("BookingController: Найдено бронирований для владельца {}: {}", userId, bookingsDto.size());
        return ResponseEntity.ok(bookingsDto);
    }

}