package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;

@Data
public class UpdateItemRequest {

    private String name;

    @Size(max = 200)
    private String description;

    private Boolean available;

    private UserDto owner;

    private ItemRequest request;

    public boolean hasName() {
        return !(name == null || name.isBlank());
    }

    public boolean hasDescription() {
        return !(description == null || description.isBlank());
    }

    public boolean hasAvailableStatus() {
        return available != null;
    }

    public boolean hasOwner() {
        return owner != null;
    }

    public boolean hasRequest() {
        return request != null;
    }

}