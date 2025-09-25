package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long>, QuerydslPredicateExecutor<Booking> {

    @Query("SELECT b FROM Booking b " +
            "LEFT JOIN FETCH b.booker " +
            "LEFT JOIN FETCH b.item " +
            "WHERE b.id = :bookingId")
    Optional<Booking> findByIdWithBookerAndItem(Long bookingId);

    Booking findFirstByBookerAndItemAndEndBeforeOrderByEndDesc(User booker, Item item, LocalDateTime now);

    Optional<Booking> findFirstByItemAndStartAfterOrderByStartAsc(Item item, LocalDateTime start);

    Optional<Booking> findFirstByItemAndEndAfterOrderByEndDesc(Item item, LocalDateTime end);

}