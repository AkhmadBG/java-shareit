package ru.practicum.shareit.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.booking.dto.NewBookingAddRequest;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.NewCommentAddRequest;
import ru.practicum.shareit.item.dto.NewItemAddRequest;
import ru.practicum.shareit.user.dto.NewUserAddRequest;

@Slf4j
@UtilityClass
public final class AppValidation {

    public static void userValidator(NewUserAddRequest newUserAddRequest) {
        log.info("AppValidation: Проверка пользователя, email={}", newUserAddRequest.getEmail());
        if (newUserAddRequest.getEmail() == null ||
                newUserAddRequest.getEmail().trim().isBlank() ||
                !newUserAddRequest.getEmail().contains("@")) {
            log.warn("AppValidation: Ошибка валидации пользователя, некорректный email={}", newUserAddRequest.getEmail());
            throw new ValidationException("электронная почта не может быть пустой и должна содержать символ @");
        }
        log.info("AppValidation: Валидация пользователя пройдена, email={}", newUserAddRequest.getEmail());
    }

    public static void itemValidator(NewItemAddRequest newItemAddRequest) {
        log.info("AppValidation: Проверка вещи, name={}, available={}", newItemAddRequest.getName(), newItemAddRequest.getAvailable());
        if (newItemAddRequest.getAvailable() == null) {
            log.warn("AppValidation: Ошибка валидации вещи: отсутствует поле доступности");
            throw new ValidationException("в запросе отсутствует поле доступности к бронированию");
        }
        if (newItemAddRequest.getName() == null || newItemAddRequest.getName().trim().isBlank()) {
            log.warn("AppValidation: Ошибка валидации вещи: отсутствует или пустое поле 'name'");
            throw new ValidationException("в запросе поле 'name' отсутствует или не задано");
        }
        if (newItemAddRequest.getDescription() == null || newItemAddRequest.getDescription().trim().isBlank()) {
            log.warn("AppValidation: Ошибка валидации вещи: отсутствует или пустое поле 'description'");
            throw new ValidationException("в запросе поле 'description' отсутствует или не задано");
        }
        log.info("AppValidation: Валидация вещи пройдена, name={}", newItemAddRequest.getName());
    }

    public static void bookingValidator(NewBookingAddRequest newBookingAddRequest) {
        log.info("AppValidation: Проверка бронирования, start={}, end={}", newBookingAddRequest.getStart(), newBookingAddRequest.getEnd());
        if (newBookingAddRequest.getStart() == null) {
            log.warn("AppValidation: Ошибка валидации бронирования: отсутствует поле начала бронирования");
            throw new ValidationException("некорректно составлен запрос на новое бронирование - отсутствует поле начала бронирования");
        }
        if (newBookingAddRequest.getEnd() == null) {
            log.warn("AppValidation: Ошибка валидации бронирования: отсутствует поле окончания бронирования");
            throw new ValidationException("некорректно составлен запрос на новое бронирование - отсутствует поле окончания бронирования");
        }
        log.info("AppValidation: Валидация бронирования пройдена, start={}, end={}", newBookingAddRequest.getStart(), newBookingAddRequest.getEnd());
    }

    public static void commentValidator(NewCommentAddRequest newCommentAddRequest) {
        log.info("AppValidation: Проверка комментария, текст={}", newCommentAddRequest.getText());
        if (newCommentAddRequest.getText().trim().isBlank()) {
            log.warn("AppValidation: Ошибка валидации комментария: текст пустой");
            throw new ValidationException("некорректно составлен запрос - отсутствует текст комментария");
        }
        log.info("AppValidation: Валидация комментария пройдена, текст={}", newCommentAddRequest.getText());
    }

}