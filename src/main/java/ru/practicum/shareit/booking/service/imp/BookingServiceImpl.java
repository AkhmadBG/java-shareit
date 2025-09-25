package ru.practicum.shareit.booking.service.imp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapStruct;
import ru.practicum.shareit.booking.dto.NewBookingAddRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.StateParam;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemMapStruct;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserMapStruct;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.AppValidation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;
    private final BookingMapStruct bookingMapStruct;
    private final UserMapStruct userMapStruct;
    private final ItemMapStruct itemMapStruct;

    @Override
    public BookingDto addBooking(Long userId, NewBookingAddRequest newBookingAddRequest) {
        log.info("BookingService: Создание бронирования: пользовательId={}, запрос={}", userId, newBookingAddRequest);
        AppValidation.bookingValidator(newBookingAddRequest);
        User user = userService.getUserById(userId);
        Item item = itemService.getItemById(newBookingAddRequest.getItemId());
        if (!item.getAvailable()) {
            log.warn("BookingService: Вещь недоступна для бронирования: itemId={}", item.getId());
            throw new RuntimeException("вещь занята");
        }
        Booking booking = bookingMapStruct.newBooking(userMapStruct.toUserDto(user),
                itemMapStruct.toItemDto(item),
                newBookingAddRequest);
        Booking newBooking = bookingRepository.save(booking);
        log.info("BookingService: Бронирование сохранено: bookingId={}", newBooking.getId());
        return bookingMapStruct.toBookingDto(newBooking);
    }

    @Override
    public BookingDto approvedBooking(Long userId, Long bookingId, Boolean approved) {
        log.info("BookingService: Подтверждение бронирования: пользовательId={}, bookingId={}, approved={}", userId, bookingId, approved);
        Booking booking = bookingRepository.findByIdWithBookerAndItem(bookingId)
                .orElseThrow(() -> new NotFoundException("booking не найден"));
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            log.warn("BookingService: Доступ запрещён. Пользователь {} не является владельцем вещи bookingId={}", userId, bookingId);
            throw new AccessException("подтверждать бронирование может только владелец вещи");
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        Booking saved = bookingRepository.save(booking);
        log.info("BookingService: Статус бронирования обновлён: bookingId={}, статус={}", saved.getId(), saved.getStatus());
        return bookingMapStruct.toBookingDto(saved);
    }

    @Override
    public BookingDto getBookingByBookerIdOrOwnerId(Long userId, Long bookingId) {
        log.info("BookingService: Получение бронирования: пользовательId={}, bookingId={}", userId, bookingId);
        Booking booking = bookingRepository.findByIdWithBookerAndItem(bookingId)
                .orElseThrow(() -> new NotFoundException("booking не найден"));
        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            log.warn("BookingService: Доступ запрещён. Пользователь {} не имеет прав на просмотр bookingId={}", userId, bookingId);
            throw new AccessException("просмотр бронирования доступен только владельцу вещи или автору бронирования");
        }
        log.info("BookingService: Бронирование найдено: bookingId={}", booking.getId());
        return bookingMapStruct.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getUserBookings(Long userId, StateParam state) {
        log.info("BookingService: Получение списка бронирований пользователя: пользовательId={}, state={}", userId, state);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = switch (state) {
            case CURRENT -> bookingRepository.findByBookerIdAndStartBeforeAndEndAfter(userId, now, now);
            case PAST -> bookingRepository.findByBookerIdAndEndBefore(userId, now);
            case FUTURE -> bookingRepository.findByBookerIdAndStartAfter(userId, now);
            case WAITING -> bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.WAITING);
            case REJECTED -> bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.REJECTED);
            default -> bookingRepository.findByBookerId(userId);
        };
        return bookings.stream()
                .map(bookingMapStruct::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getBookingsForItemsByOwnerId(Long userId, StateParam state) {
        log.info("BookingService: Получение бронирований для вещей владельца: владелецId={}, state={}", userId, state);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = switch (state) {
            case CURRENT ->
                    bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now);
            case PAST -> bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, now);
            case FUTURE -> bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(userId, now);
            case WAITING -> bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
            case REJECTED ->
                    bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
            default -> bookingRepository.findByItemOwnerIdOrderByStartDesc(userId);
        };
        log.info("BookingService: Найдено бронирований для владельца {}: {}", userId, bookings.size());
        return bookings.stream()
                .map(bookingMapStruct::toBookingDto)
                .toList();
    }

}