package ru.practicum.shareit.booking.service.imp;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapStruct;
import ru.practicum.shareit.booking.dto.NewBookingAddRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.QBooking;
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
        QBooking booking = QBooking.booking;
        LocalDateTime now = LocalDateTime.now();
        BooleanExpression predicate = booking.booker.id.eq(userId);
        switch (state) {
            case CURRENT -> predicate = predicate
                    .and(booking.start.loe(now))
                    .and(booking.end.goe(now));
            case PAST -> predicate = predicate.and(booking.end.before(now));
            case FUTURE -> predicate = predicate.and(booking.start.after(now));
            case WAITING -> predicate = predicate.and(booking.status.eq(BookingStatus.WAITING));
            case REJECTED -> predicate = predicate.and(booking.status.eq(BookingStatus.REJECTED));
            case StateParam.All -> {
            }
        }

        List<Booking> bookingList = (List<Booking>) bookingRepository.findAll(
                predicate,
                Sort.by(Sort.Direction.DESC, "start")
        );
        log.info("BookingService: Найдено бронирований для пользователя {}: {}", userId, bookingList.size());
        return bookingList.stream()
                .map(bookingMapStruct::toBookingDto)
                .toList();
    }

    @Override
    public List<BookingDto> getBookingsForItemsByOwnerId(Long userId, StateParam state) {
        log.info("BookingService: Получение бронирований для вещей владельца: владелецId={}, state={}", userId, state);
        QBooking booking = QBooking.booking;
        LocalDateTime now = LocalDateTime.now();
        BooleanExpression predicate = booking.item.owner.id.eq(userId);
        switch (state) {
            case CURRENT -> predicate = predicate
                    .and(booking.start.loe(now))
                    .and(booking.end.goe(now));
            case PAST -> predicate = predicate.and(booking.end.before(now));
            case FUTURE -> predicate = predicate.and(booking.start.after(now));
            case WAITING -> predicate = predicate.and(booking.status.eq(BookingStatus.WAITING));
            case REJECTED -> predicate = predicate.and(booking.status.eq(BookingStatus.REJECTED));
            case StateParam.All -> {
            }
        }

        List<Booking> bookingList = (List<Booking>) bookingRepository.findAll(
                predicate,
                Sort.by(Sort.Direction.DESC, "start")
        );
        log.info("BookingService: Найдено бронирований для владельца {}: {}", userId, bookingList.size());
        return bookingList.stream()
                .map(bookingMapStruct::toBookingDto)
                .toList();
    }

}