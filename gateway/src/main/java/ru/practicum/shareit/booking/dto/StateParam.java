package ru.practicum.shareit.booking.dto;

public enum StateParam {

    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static StateParam fromString(String value) {
        try {
            return StateParam.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            return StateParam.ALL;
        }
    }

}