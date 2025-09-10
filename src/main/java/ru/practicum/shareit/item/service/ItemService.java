package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemAddRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

import java.util.List;

public interface ItemService {

    ItemDto addItem(Long userId, NewItemAddRequest newItemAddRequest);

    ItemDto updateItem(Long itemId, Long userId, UpdateItemRequest updateItemRequest);

    ItemDto getItemById(Long itemId);

    List<ItemDto> getItemsByUserId(Long userId);

    List<ItemDto> getAllItems();

    void deleteItem(Long itemId);

    List<ItemDto> searchItems(String text);

}