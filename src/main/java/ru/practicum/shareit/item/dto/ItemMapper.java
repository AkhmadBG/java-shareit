package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(item.getOwner())
                .request(item.getRequest())
                .build();
    }

    public static Item toItem(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(itemDto.getOwner())
                .request(itemDto.getRequest())
                .build();
    }

    public static Item newItem(NewItemAddRequest newItemAddRequest, User owner) {
        return Item.builder()
                .name(newItemAddRequest.getName())
                .description(newItemAddRequest.getDescription())
                .available(newItemAddRequest.getAvailable())
                .owner(owner)
                .request(newItemAddRequest.getRequest())
                .build();
    }

    public static void updateItem(Item item, UpdateItemRequest updateItemRequest) {
        if (updateItemRequest.hasName()) {
            item.setName(updateItemRequest.getName());
        }
        if (updateItemRequest.hasDescription()) {
            item.setDescription(updateItemRequest.getDescription());
        }
        if (updateItemRequest.hasAvailableStatus()) {
            item.setAvailable(updateItemRequest.getAvailable());
        }
        if (updateItemRequest.hasOwner()) {
            item.setOwner(updateItemRequest.getOwner());
        }
        if (updateItemRequest.hasRequest()) {
            item.setRequest(updateItemRequest.getRequest());
        }
    }

}