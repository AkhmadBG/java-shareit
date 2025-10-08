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
    public ResponseEntity<RequestDto> addRequest(@RequestHeader(CUSTOM_REQUEST_HEADER_USER_ID) Long userId,
                                                 @RequestBody NewRequest newRequest) {
        RequestDto requestDto = requestService.addRequest(userId, newRequest);
        return ResponseEntity.ok(requestDto);
    }

    @GetMapping
    public ResponseEntity<List<RequestDto>> getRequestsByUserId(@RequestHeader(CUSTOM_REQUEST_HEADER_USER_ID) Long userId) {
        List<RequestDto> itemRequests = requestService.getRequestsByUserId(userId);
        return ResponseEntity.ok(itemRequests);
    }

    @GetMapping("/all")
    public ResponseEntity<List<RequestDto>> getRequestsOtherUsers(@RequestHeader(CUSTOM_REQUEST_HEADER_USER_ID) Long userId) {
        List<RequestDto> itemRequestsOtherUsers = requestService.getRequestsOtherUsers(userId);
        return ResponseEntity.ok(itemRequestsOtherUsers);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<RequestDto> getRequestById(@RequestHeader(CUSTOM_REQUEST_HEADER_USER_ID) Long userId,
                                                     @PathVariable(name = "requestId") Long requestId) {
        RequestDto requestDto = requestService.getRequestDtoById(userId, requestId);
        return ResponseEntity.ok(requestDto);
    }

}