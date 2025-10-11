package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NewItemAddRequest {

    @NotBlank
    private String name;

    @Size(max = 200)
    private String description;

    private Boolean available;

    private Long requestId;

}