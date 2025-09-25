package ru.practicum.shareit.booking.model;

public enum StateParam {

    All,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static StateParam fromString(String value) {
        try {
            return StateParam.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            return StateParam.All;
        }
    }

}