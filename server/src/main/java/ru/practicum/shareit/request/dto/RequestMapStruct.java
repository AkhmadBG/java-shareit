package ru.practicum.shareit.request.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.shareit.item.dto.ItemMapStruct;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {ItemMapStruct.class})
public interface RequestMapStruct {

    RequestDto toRequestDto(Request request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "description", source = "newRequest.description")
    @Mapping(target = "requestor", source = "user")
    @Mapping(target = "created", source = "localDateTime")
    Request newRequest(User user, NewRequest newRequest, LocalDateTime localDateTime);

}