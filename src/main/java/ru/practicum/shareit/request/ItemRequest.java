package ru.practicum.shareit.request;

import jakarta.validation.constraints.PastOrPresent;
import lombok.*;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Setter
@Getter
@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequest {

    private Long id;

    private String description;

    private User requestor;

    @PastOrPresent(message = "Время и дата не может быть в прошлом")
    private LocalDateTime created;

}