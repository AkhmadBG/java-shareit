package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking b " +
            "LEFT JOIN FETCH b.booker " +
            "LEFT JOIN FETCH b.item " +
            "WHERE b.id = :bookingId")
    Optional<Booking> findByIdWithBookerAndItem(Long bookingId);

    Booking findFirstByBookerAndItemAndEndBeforeOrderByEndDesc(User booker, Item item, LocalDateTime now);

    Optional<Booking> findFirstByItemAndStartAfterOrderByStartAsc(Item item, LocalDateTime start);

    Optional<Booking> findFirstByItemAndEndAfterOrderByEndDesc(Item item, LocalDateTime end);

    List<Booking> findByBookerId(Long userId);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfter(Long userId, LocalDateTime start, LocalDateTime end);

    List<Booking> findByBookerIdAndEndBefore(Long userId, LocalDateTime end);

    List<Booking> findByBookerIdAndStartAfter(Long userId, LocalDateTime start);

    List<Booking> findByBookerIdAndStatus(Long userId, BookingStatus status);

    List<Booking> findByItemOwnerIdOrderByStartDesc(Long ownerId);

    List<Booking> findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long ownerId, LocalDateTime start, LocalDateTime end);

    List<Booking> findByItemOwnerIdAndEndBeforeOrderByStartDesc(Long ownerId, LocalDateTime end);

    List<Booking> findByItemOwnerIdAndStartAfterOrderByStartDesc(Long ownerId, LocalDateTime start);

    List<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(Long ownerId, BookingStatus status);

}