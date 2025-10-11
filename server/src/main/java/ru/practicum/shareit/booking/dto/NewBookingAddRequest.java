package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NewBookingAddRequest {

    @NotBlank
    private Long itemId;

    @NotBlank
    private LocalDateTime start;

    @NotBlank
    private LocalDateTime end;

}