package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemAddRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static ru.practicum.shareit.util.AppConstant.CUSTOM_REQUEST_HEADER_USER_ID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemDto> addItem(@RequestHeader(CUSTOM_REQUEST_HEADER_USER_ID) Long userId,
                                           @RequestBody NewItemAddRequest newItemAddRequest) {
        ItemDto itemDto = itemService.addItem(userId, newItemAddRequest);
        log.info("ItemController: вещь c id = {} добавлена пользователю с id = {}", itemDto.getId(), userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(@PathVariable(value = "itemId") Long itemId,
                                              @RequestHeader(CUSTOM_REQUEST_HEADER_USER_ID) Long userId,
                                              @RequestBody UpdateItemRequest updateItemRequest) {
        log.info("ItemController: попытка обновить вещь c id = {} пользователя с id = {} обновлена", itemId, userId);
        ItemDto itemDto = itemService.updateItem(itemId, userId, updateItemRequest);
        log.info("ItemController: вещь c id = {} пользователя с id = {} обновлена", itemId, userId);
        return ResponseEntity.ok(itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItemById(@PathVariable(value = "itemId") Long itemId,
                                               @RequestHeader(CUSTOM_REQUEST_HEADER_USER_ID) Long userId) {
        ItemDto itemDto = itemService.getItemById(itemId);
        log.info("ItemController: получение вещи c id = {} пользователя с id = {}", itemId, userId);
        return ResponseEntity.ok(itemDto);
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getItemsByUserId(@RequestHeader(CUSTOM_REQUEST_HEADER_USER_ID) Long userId) {
        List<ItemDto> usersItemDto = itemService.getItemsByUserId(userId);
        log.info("ItemController: получение списка вещей пользователя с id = {}", userId);
        return ResponseEntity.ok(usersItemDto);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteItem(@PathVariable(value = "itemId") Long itemId,
                                           @RequestHeader(CUSTOM_REQUEST_HEADER_USER_ID) Long userId) {
        itemService.deleteItem(itemId);
        log.info("ItemController: удаление вещи c id = {} пользователя с id = {}", itemId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItems(@RequestParam(value = "text") String text) {
        List<ItemDto> searchItem = itemService.searchItems(text);
        log.info("ItemController: поиск вещи, в названии или описании которого встречается {}", text);
        return ResponseEntity.ok(searchItem);
    }

}