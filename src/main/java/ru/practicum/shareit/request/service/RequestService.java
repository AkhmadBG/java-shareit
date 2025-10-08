package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.NewRequest;
import ru.practicum.shareit.request.model.Request;

import java.util.List;

public interface RequestService {

    RequestDto addRequest(Long userId, NewRequest newRequest);

    List<RequestDto> getRequestsByUserId(Long userId);

    List<RequestDto> getRequestsOtherUsers(Long userId);

    Request getRequestById(Long requestId);

    RequestDto getRequestDtoById(Long userId, Long requestId);

}
