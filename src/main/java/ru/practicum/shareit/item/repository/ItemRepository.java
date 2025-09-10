package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemRepository {

    Item addItem(Item item, User owner);

    Item getItemById(Long itemId);

    List<Item> getItemsByUserId(Long userId);

    void updateItem(Item item);

    List<Item> getAllItems();

    void deleteItem(Long itemId);

    List<Item> searchItems(String text);

}