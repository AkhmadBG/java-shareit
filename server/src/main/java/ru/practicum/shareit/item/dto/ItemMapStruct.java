package ru.practicum.shareit.item.dto;

import org.mapstruct.*;
import ru.practicum.shareit.item.model.Item;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ItemMapStruct {

    ItemDto toItemDto(Item item);

    ItemDtoWithDate itemDtoWithDate(Item item);

    @Mapping(target = "itemId", source = "item.id")
    @Mapping(target = "name", source = "item.name")
    @Mapping(target = "ownerId", source = "item.owner.id")
    ItemDtoForRequest itemDtoForRequest(Item item);

    @Mapping(target = "name", source = "newItemAddRequest.name")
    Item newItem(NewItemAddRequest newItemAddRequest);

    void updateItem(@MappingTarget Item item, UpdateItemRequest updateItemRequest);

}