package ru.practicum.shareit.item.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

@Setter
@Getter
@Builder
@ToString
@EqualsAndHashCode(exclude = "owner")
@AllArgsConstructor
@NoArgsConstructor
public class Item {

    private Long id;

    @NotBlank
    @NotEmpty
    @NotNull
    private String name;

    @Size(max = 200)
    @NotBlank
    @NotEmpty
    @NotNull
    private String description;

    private Boolean available;

    private User owner;

    private ItemRequest request;

}