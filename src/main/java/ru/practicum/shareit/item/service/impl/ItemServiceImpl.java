package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.NewItemAddRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.AppValidation;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto addItem(Long userId, NewItemAddRequest newItemAddRequest) {
        User owner = userRepository.getUserById(userId);
        if (owner == null) {
            throw new NotFoundException("Владелец вещи не найден или не существует");
        }
        AppValidation.itemValidator(newItemAddRequest);
        Item item = itemRepository.addItem(ItemMapper.newItem(newItemAddRequest, owner), owner);
        log.info("ItemServiceImpl: вещь c id = {} добавлена пользователю с id = {}", item.getId(), userId);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(Long itemId, Long userId, UpdateItemRequest updateItemRequest) {
        Item item = itemRepository.getItemById(itemId);
        if (!userId.equals(item.getOwner().getId())) {
            throw new AccessException("изменить вещь может только владелец");
        }
        ItemMapper.updateItem(item, updateItemRequest);
        itemRepository.updateItem(item);
        log.info("ItemServiceImpl: вещь c id = {} пользователя с id = {} обновлена", item.getId(), userId);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        Item Item = itemRepository.getItemById(itemId);
        log.info("ItemServiceImpl: получение вещи c id = {} ", itemId);
        return ItemMapper.toItemDto(Item);
    }

    @Override
    public List<ItemDto> getItemsByUserId(Long userId) {
        List<Item> userItems = itemRepository.getItemsByUserId(userId);
        log.info("ItemServiceImpl: получение списка вещей пользователя с id = {}", userId);
        return userItems.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getAllItems() {
        List<Item> allItems = itemRepository.getAllItems();
        log.info("ItemServiceImpl: получение списка всех вещей, всего {} вещей", allItems.size());
        return allItems.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteItem(Long itemId) {
        itemRepository.deleteItem(itemId);
        log.info("ItemServiceImpl: удаление вещи c id = {}", itemId);
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        List<Item> searchItem = itemRepository.searchItems(text);
        log.info("ItemServiceImpl: поиск вещей, в названии или описании которого встречается {}", text);
        return searchItem.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

}