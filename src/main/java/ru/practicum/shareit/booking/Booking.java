package ru.practicum.shareit.booking;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Setter
@Getter
@ToString
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Booking {

    private Long id;

    @FutureOrPresent(message = "Дата начала бронирования бронирования должна быть текущей или в будущем")
    private LocalDateTime start;

    @Future(message = "Дата окончания бронирования бронирования должна быть в будущем")
    private LocalDateTime end;

    private Item item;

    private User booker;

    private BookingStatus bookingStatus;

}