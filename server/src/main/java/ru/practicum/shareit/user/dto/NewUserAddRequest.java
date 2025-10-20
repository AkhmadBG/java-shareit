package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class NewUserAddRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String email;

}