package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.NewRequest;
import ru.practicum.shareit.request.service.RequestService;

import java.util.List;

import static ru.practicum.shareit.util.AppConstant.CUSTOM_REQUEST_HEADER_USER_ID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
public class RequestController {

    private final RequestService requestService;

    @PostMapping
    public ResponseEntity<RequestDto> addRequest(
            @RequestHeader(CUSTOM_REQUEST_HEADER_USER_ID) Long userId,
            @RequestBody NewRequest newRequest) {
        log.info("RequestController: Добавление новой заявки пользователем userId={}, запрос={}", userId, newRequest);
        RequestDto requestDto = requestService.addRequest(userId, newRequest);
        log.info("RequestController: Заявка успешно добавлена: requestId={}, userId={}", requestDto.getId(), userId);
        return ResponseEntity.ok(requestDto);
    }

    @GetMapping
    public ResponseEntity<List<RequestDto>> getRequestsByUserId(
            @RequestHeader(CUSTOM_REQUEST_HEADER_USER_ID) Long userId) {
        log.info("RequestController: Получение заявок пользователя userId={}", userId);
        List<RequestDto> itemRequests = requestService.getRequestsByUserId(userId);
        log.info("RequestController: Найдено {} заявок пользователя userId={}", itemRequests.size(), userId);
        return ResponseEntity.ok(itemRequests);
    }

    @GetMapping("/all")
    public ResponseEntity<List<RequestDto>> getRequestsOtherUsers(
            @RequestHeader(CUSTOM_REQUEST_HEADER_USER_ID) Long userId) {
        log.info("RequestController: Получение заявок других пользователей для userId={}", userId);
        List<RequestDto> itemRequestsOtherUsers = requestService.getRequestsOtherUsers(userId);
        log.info("RequestController: Найдено {} заявок других пользователей для userId={}", itemRequestsOtherUsers.size(), userId);
        return ResponseEntity.ok(itemRequestsOtherUsers);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<RequestDto> getRequestById(
            @RequestHeader(CUSTOM_REQUEST_HEADER_USER_ID) Long userId,
            @PathVariable(name = "requestId") Long requestId) {
        log.info("RequestController: Получение заявки по id: userId={}, requestId={}", userId, requestId);
        RequestDto requestDto = requestService.getRequestDtoById(userId, requestId);
        log.info("RequestController: Заявка найдена: requestId={}, userId={}", requestDto.getId(), userId);
        return ResponseEntity.ok(requestDto);
    }

}