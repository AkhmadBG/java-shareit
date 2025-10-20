package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.RequestClient;
import ru.practicum.shareit.request.dto.NewRequest;

import static ru.practicum.shareit.util.AppConstant.CUSTOM_REQUEST_HEADER_USER_ID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
public class RequestController {

    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> addRequest(@RequestHeader(CUSTOM_REQUEST_HEADER_USER_ID) Long userId,
                                             @RequestBody NewRequest newRequest) {
        log.info("RequestController: Добавление новой заявки пользователем userId={}, запрос={}", userId, newRequest);
        return requestClient.addRequest(userId, newRequest);
    }

    @GetMapping
    public ResponseEntity<Object> getRequestsByUserId(@RequestHeader(CUSTOM_REQUEST_HEADER_USER_ID) Long userId) {
        log.info("RequestController: Получение заявок пользователя userId={}", userId);
        return requestClient.getRequestsByUserId(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getRequestsOtherUsers(@RequestHeader(CUSTOM_REQUEST_HEADER_USER_ID) Long userId) {
        log.info("RequestController: Получение заявок других пользователей для userId={}", userId);
        return requestClient.getRequestsOtherUsers(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader(CUSTOM_REQUEST_HEADER_USER_ID) Long userId,
                                                 @PathVariable(name = "requestId") Long requestId) {
        log.info("RequestController: Получение заявки по id: userId={}, requestId={}", userId, requestId);
        return requestClient.getRequestDtoById(userId, requestId);
    }

}