package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.AppValidation;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemMapStruct itemMapStruct;
    private final CommentMapStruct commentMapStruct;

    @Override
    public ItemDto addItem(Long userId, NewItemAddRequest newItemAddRequest) {
        log.info("ItemService: Создание вещи: пользовательId={}, запрос={}", userId, newItemAddRequest);
        AppValidation.itemValidator(newItemAddRequest);
        User owner = userService.getUserById(userId);
        Item item = itemMapStruct.newItem(newItemAddRequest);
        item.setOwner(owner);
        Item newItem = itemRepository.save(item);
        log.info("ItemService: Вещь создана: itemId={}, пользовательId={}", newItem.getId(), userId);
        return itemMapStruct.toItemDto(newItem);
    }

    @Override
    public ItemDto updateItem(Long itemId, Long userId, UpdateItemRequest updateItemRequest) {
        log.info("ItemService: Обновление вещи: itemId={}, пользовательId={}, запрос={}", itemId, userId, updateItemRequest);
        Item item = itemRepository.findByIdWithOwnerAndRequest(itemId)
                .orElseThrow(() -> new NotFoundException("вещь c id = " + itemId + " не найдена"));
        if (!userId.equals(item.getOwner().getId())) {
            log.warn("ItemService: Доступ запрещён. Пользователь {} не является владельцем itemId={}", userId, itemId);
            throw new AccessException("изменить вещь может только владелец");
        }
        User owner = userService.getUserById(userId);
        item.setOwner(owner);
        itemMapStruct.updateItem(item, updateItemRequest);
        Item updated = itemRepository.save(item);
        log.info("ItemService: Вещь обновлена: itemId={}, пользовательId={}", updated.getId(), userId);
        return itemMapStruct.toItemDto(updated);
    }

    @Override
    public ItemDtoWithDate getItemDtoWithDateById(Long itemId) {
        log.info("ItemService: Получение вещи с датами бронирований: itemId={}", itemId);
        Item item = itemRepository.findByIdWithOwnerAndRequest(itemId)
                .orElseThrow(() -> new NotFoundException("вещь c id = " + itemId + " не найдена"));
        Optional<Booking> nextBooking = bookingRepository
                .findFirstByItemAndStartAfterOrderByStartAsc(item, LocalDateTime.now());
        Optional<Booking> lastBooking = bookingRepository
                .findFirstByItemAndEndAfterOrderByEndDesc(item, LocalDateTime.now());

        ItemDtoWithDate itemDtoWithDate = itemMapStruct.itemDtoWithDate(item);
        itemDtoWithDate.setNextBooking(nextBooking.map(Booking::getStart).orElse(null));
        itemDtoWithDate.setLastBooking(lastBooking.map(Booking::getStart).orElse(null));
        log.info("ItemService: Вещь найдена: itemId={}", item.getId());
        return itemDtoWithDate;
    }

    @Override
    public List<ItemDto> getItemsByUserId(Long userId) {
        log.info("ItemService: Получение списка вещей пользователя: пользовательId={}", userId);
        List<Item> userItems = itemRepository.findItemByOwnerId(userId);
        log.info("ItemService: Найдено вещей у пользователя {}: {}", userId, userItems.size());
        return userItems.stream()
                .map(itemMapStruct::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteItem(Long itemId) {
        log.info("ItemService: Удаление вещи: itemId={}", itemId);
        itemRepository.deleteById(itemId);
        log.info("ItemService: Вещь удалена: itemId={}", itemId);
    }

    @Override
    public List<ItemDto> searchItemsByText(String text) {
        log.info("ItemService: Поиск вещей по тексту: '{}'", text);
        if (text.trim().isBlank()) {
            log.info("ItemService: Пустая строка поиска — возврат пустого списка");
            return Collections.emptyList();
        }
        List<Item> searchItem = itemRepository.searchItemsByText(text);
        log.info("ItemService: Найдено вещей по запросу '{}': {}", text, searchItem.size());
        return searchItem.stream()
                .map(itemMapStruct::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public Item getItemById(Long itemId) {
        log.info("ItemService: Получение вещи по id: {}", itemId);
        return itemRepository.findByIdWithOwnerAndRequest(itemId)
                .orElseThrow(() -> new NotFoundException("вещь c id = " + itemId + " не найдена"));
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, NewCommentAddRequest newCommentAddRequest) {
        log.info("ItemService: Добавление комментария: пользовательId={}, itemId={}, запрос={}", userId, itemId, newCommentAddRequest);
        AppValidation.commentValidator(newCommentAddRequest);
        User author = userService.getUserById(userId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("item not found"));

        Booking booking = bookingRepository
                .findFirstByBookerAndItemAndEndBeforeOrderByEndDesc(author, item, LocalDateTime.now());

        if (!booking.getBooker().getId().equals(author.getId())) {
            log.warn("ItemService: Пользователь {} не может оставить комментарий к itemId={}", userId, itemId);
            throw new ValidationException("пользователь не может оставить комментарий");
        }
        if (booking.getEnd().isAfter(LocalDateTime.now())) {
            log.warn("ItemService: Попытка оставить комментарий до завершения бронирования: userId={}, itemId={}", userId, itemId);
            throw new ValidationException("нельзя оставить комментарий до завершения бронирования");
        }

        Comment comment = commentMapStruct.newComment(author, item, newCommentAddRequest);
        comment.setCreated(LocalDateTime.now());
        Comment newComment = commentRepository.save(comment);
        log.info("ItemService: Комментарий добавлен: commentId={}, itemId={}, userId={}", newComment.getId(), itemId, userId);
        return commentMapStruct.toCommentDto(newComment);
    }

}