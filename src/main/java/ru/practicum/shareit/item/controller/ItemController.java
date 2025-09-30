package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static ru.practicum.shareit.util.AppConstant.CUSTOM_REQUEST_HEADER_USER_ID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemDto> addItem(@RequestHeader(CUSTOM_REQUEST_HEADER_USER_ID) Long userId,
                                           @RequestBody NewItemAddRequest newItemAddRequest) {
        log.info("ItemController: Создание вещи: пользовательId={}, запрос={}", userId, newItemAddRequest);
        ItemDto itemDto = itemService.addItem(userId, newItemAddRequest);
        log.info("ItemController: Вещь создана: itemId={}, пользовательId={}", itemDto.getId(), userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(@PathVariable(value = "itemId") Long itemId,
                                              @RequestHeader(CUSTOM_REQUEST_HEADER_USER_ID) Long userId,
                                              @RequestBody UpdateItemRequest updateItemRequest) {
        log.info("ItemController: Обновление вещи: itemId={}, пользовательId={}, запрос={}", itemId, userId, updateItemRequest);
        ItemDto itemDto = itemService.updateItem(itemId, userId, updateItemRequest);
        log.info("ItemController: Вещь обновлена: itemId={}, пользовательId={}", itemDto.getId(), userId);
        return ResponseEntity.ok(itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDtoWithDate> getItemById(@PathVariable(value = "itemId") Long itemId,
                                                       @RequestHeader(CUSTOM_REQUEST_HEADER_USER_ID) Long userId) {
        log.info("ItemController: Получение вещи: itemId={}, пользовательId={}", itemId, userId);
        ItemDtoWithDate itemDtoWithDate = itemService.getItemDtoWithDateById(itemId);
        log.info("ItemController: Вещь найдена: itemId={}, пользовательId={}", itemDtoWithDate.getId(), userId);
        return ResponseEntity.ok(itemDtoWithDate);
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getItemsByUserId(@RequestHeader(CUSTOM_REQUEST_HEADER_USER_ID) Long userId) {
        log.info("ItemController: Получение списка вещей пользователя: пользовательId={}", userId);
        List<ItemDto> usersItemDto = itemService.getItemsByUserId(userId);
        log.info("ItemController: Найдено вещей у пользователя {}: {}", userId, usersItemDto.size());
        return ResponseEntity.ok(usersItemDto);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteItem(@PathVariable(value = "itemId") Long itemId,
                                           @RequestHeader(CUSTOM_REQUEST_HEADER_USER_ID) Long userId) {
        log.info("ItemController: Удаление вещи: itemId={}, пользовательId={}", itemId, userId);
        itemService.deleteItem(itemId);
        log.info("ItemController: Вещь удалена: itemId={}, пользовательId={}", itemId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItems(@RequestParam(value = "text") String text) {
        log.info("ItemController: Поиск вещей по тексту: '{}'", text);
        List<ItemDto> searchItem = itemService.searchItemsByText(text);
        log.info("ItemController: Найдено вещей по запросу '{}': {}", text, searchItem.size());
        return ResponseEntity.ok(searchItem);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> addComment(@RequestHeader(CUSTOM_REQUEST_HEADER_USER_ID) Long userId,
                                                 @PathVariable(value = "itemId") Long itemId,
                                                 @RequestBody NewCommentAddRequest newCommentAddRequest) {
        log.info("ItemController: Добавление комментария: itemId={}, пользовательId={}, запрос={}", itemId, userId, newCommentAddRequest);
        CommentDto commentDto = itemService.addComment(userId, itemId, newCommentAddRequest);
        log.info("ItemController: Комментарий добавлен: commentId={}, itemId={}, пользовательId={}", commentDto.getId(), itemId, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(commentDto);
    }

}