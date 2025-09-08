package ru.practicum.shareit.util;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.NewItemAddRequest;
import ru.practicum.shareit.user.dto.NewUserAddRequest;

@UtilityClass
public final class AppValidation {

    public static void userValidator(NewUserAddRequest newUserAddRequest) {
        if (newUserAddRequest.getEmail() == null ||
                newUserAddRequest.getEmail().isBlank() ||
                !newUserAddRequest.getEmail().contains("@")) {
            throw new ValidationException("электронная почта не может быть пустой и должна содержать символ @");
        }
    }

    public static void itemValidator(NewItemAddRequest newItemAddRequest) {
        if (newItemAddRequest.getAvailable() == null) {
            throw new ValidationException("в запросе отсутствует поле доступности к бронированию");
        }
        if (newItemAddRequest.getName() == null ||
                newItemAddRequest.getName().isBlank()) {
            throw new ValidationException("в запросе поле 'name' отсутствует или не задано");
        }
        if (newItemAddRequest.getDescription() == null ||
                newItemAddRequest.getDescription().isBlank()) {
            throw new ValidationException("в запросе поле 'name' отсутствует или не задано");
        }
    }

}