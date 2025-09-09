package ru.practicum.shareit.item.dto;

import org.mapstruct.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ItemMapStruct {

    ItemDto toItemDto(Item item);

    @Mapping(target = "name", source = "newItemAddRequest.name")
    Item newItem(NewItemAddRequest newItemAddRequest, UserDto userDto);

    void updateItem(@MappingTarget Item item, UpdateItemRequest updateItemRequest);

}