package ru.practicum.shareit.item.repository.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserMapStruct;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class InMemoryItemRepository implements ItemRepository {

    private final Map<Long, Item> items = new HashMap<>();
    private Long identifier = 0L;
    private final UserMapStruct userMapStruct;

    @Override
    public Item addItem(Item item, User owner) {
        item.setOwner(owner);
        item.setId(getIdentifier());
        items.put(item.getId(), item);
        log.info("InMemoryItemRepository: вещь c id = {} добавлена пользователю с id = {}", item.getId(), owner.getId());
        return items.get(identifier);
    }

    @Override
    public Item getItemById(Long itemId) {
        Item item = items.get(itemId);
        log.info("InMemoryItemRepository: получение вещи c id = {} ", itemId);
        return item;
    }

    @Override
    public List<Item> getItemsByUserId(Long userId) {
        log.info("InMemoryItemRepository: получение списка вещей пользователя с id = {}", userId);
        return items.values().stream()
                .filter(item -> item.getOwner() != null &&
                        item.getOwner().getId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public void updateItem(Item item) {
        items.put(item.getId(), item);
        log.info("InMemoryItemRepository: вещь c id = {} пользователя с id = {} обновлена", item.getId(), item.getOwner().getId());
    }

    @Override
    public List<Item> getAllItems() {
        Collection<Item> allItems = items.values();
        log.info("InMemoryItemRepository: получение списка всех вещей, всего {} вещей", allItems.size());
        return new ArrayList<>(allItems);
    }

    @Override
    public void deleteItem(Long itemId) {
        items.remove(itemId);
        log.info("InMemoryItemRepository: удаление вещи c id = {}", itemId);
    }

    @Override
    public List<Item> searchItems(String text) {
        if (text == null || text.trim().isBlank()) {
            return Collections.emptyList();
        }
        String query = text.trim().toLowerCase();
        List<Item> foundItems = items.values().stream()
                .filter(item -> item.getName().toLowerCase().contains(query) ||
                        item.getDescription().toLowerCase().contains(query))
                .filter(item -> item.getAvailable() == true)
                .collect(Collectors.toList());
        log.info("ItemServiceImpl: при поиске вещи по запросу '{}', найдено {} вещей", query, foundItems.size());
        return foundItems;
    }

    private Long getIdentifier() {
        return ++identifier;
    }

}