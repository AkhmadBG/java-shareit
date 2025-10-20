package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    ItemDto addItem(Long userId, NewItemAddRequest newItemAddRequest);

    ItemDto updateItem(Long itemId, Long userId, UpdateItemRequest updateItemRequest);

    ItemDtoWithDate getItemDtoWithDateById(Long itemId);

    List<ItemDto> getItemsByUserId(Long userId);

    void deleteItem(Long itemId);

    List<ItemDto> searchItemsByText(String text);

    Item getItemById(Long itemId);

    CommentDto addComment(Long userId, Long itemId, NewCommentAddRequest newCommentAddRequest);

}