package ru.practicum.shareit.booking.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface BookingMapStruct {

    BookingDto toBookingDto(Booking booking);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "start", source = "newBookingAddRequest.start")
    @Mapping(target = "end", source = "newBookingAddRequest.end")
    @Mapping(target = "booker", source = "userDto")
    @Mapping(target = "item", source = "itemDto")
    @Mapping(target = "status", constant = "WAITING")
    Booking newBooking(UserDto userDto, ItemDto itemDto, NewBookingAddRequest newBookingAddRequest);

}