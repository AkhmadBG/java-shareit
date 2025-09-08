package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

@Data
public class NewItemAddRequest {

    @NotBlank
    @NotEmpty
    @NotNull
    private String name;

    @Size(max = 200)
    private String description;

    private Boolean available;

    private User owner;

    private ItemRequest request;

}