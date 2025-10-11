package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UpdateUserRequest {

    private String name;

    private String email;

    public boolean hasName() {
        return !(name == null || name.isBlank());
    }

    public boolean hasEmail() {
        return !(email == null || email.isBlank());
    }

}