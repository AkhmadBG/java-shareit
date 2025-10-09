package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NewItemAddRequest {

    @NotBlank
    private String name;

    @Size(max = 200)
    private String description;

    private Boolean available;

    private Long requestId;

}