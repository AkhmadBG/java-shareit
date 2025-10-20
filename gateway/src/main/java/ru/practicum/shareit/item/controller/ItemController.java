package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.util.AppValidation;

import static ru.practicum.shareit.util.AppConstant.CUSTOM_REQUEST_HEADER_USER_ID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/items")
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader(CUSTOM_REQUEST_HEADER_USER_ID) Long userId,
                                          @RequestBody NewItemAddRequest newItemAddRequest) {
        AppValidation.itemValidator(newItemAddRequest);
        log.info("ItemController: Создание вещи: пользовательId={}, запрос={}", userId, newItemAddRequest);
        return itemClient.addItem(userId, newItemAddRequest);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@PathVariable(value = "itemId") Long itemId,
                                             @RequestHeader(CUSTOM_REQUEST_HEADER_USER_ID) Long userId,
                                             @RequestBody UpdateItemRequest updateItemRequest) {
        log.info("ItemController: Обновление вещи: itemId={}, пользовательId={}, запрос={}", itemId, userId, updateItemRequest);
        return itemClient.updateItem(itemId, userId, updateItemRequest);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable(value = "itemId") Long itemId,
                                              @RequestHeader(CUSTOM_REQUEST_HEADER_USER_ID) Long userId) {
        log.info("ItemController: Получение вещи: itemId={}, пользовательId={}", itemId, userId);
        return itemClient.getItemDtoWithDateById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsByUserId(@RequestHeader(CUSTOM_REQUEST_HEADER_USER_ID) Long userId) {
        log.info("ItemController: Получение списка вещей пользователя: пользовательId={}", userId);
        return itemClient.getItemsByUserId(userId);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> deleteItem(@PathVariable(value = "itemId") Long itemId,
                                             @RequestHeader(CUSTOM_REQUEST_HEADER_USER_ID) Long userId) {
        log.info("ItemController: Удаление вещи: itemId={}, пользовательId={}", itemId, userId);
        return itemClient.deleteItem(itemId, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestHeader(CUSTOM_REQUEST_HEADER_USER_ID) Long userId,
                                              @RequestParam(value = "text") String text) {
        log.info("ItemController: Поиск вещей по тексту: '{}'", text);
        return itemClient.searchItemsByText(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(CUSTOM_REQUEST_HEADER_USER_ID) Long userId,
                                             @PathVariable(value = "itemId") Long itemId,
                                             @RequestBody NewCommentAddRequest newCommentAddRequest) {
        AppValidation.commentValidator(newCommentAddRequest);
        log.info("ItemController: Добавление комментария: itemId={}, пользовательId={}, запрос={}", itemId, userId, newCommentAddRequest);
        return itemClient.addComment(userId, itemId, newCommentAddRequest);
    }

}