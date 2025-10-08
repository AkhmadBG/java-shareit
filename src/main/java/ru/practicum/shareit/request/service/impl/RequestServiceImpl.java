package ru.practicum.shareit.request.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.NewRequest;
import ru.practicum.shareit.request.dto.RequestMapStruct;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserService userService;
    private final RequestMapStruct requestMapStruct;

    public RequestDto addRequest(Long userId, NewRequest newRequest) {
        User requestor = userService.getUserById(userId);
        Request request = requestMapStruct.newRequest(requestor,newRequest, LocalDateTime.now());
        Request requestNew = requestRepository.save(request);
        return requestMapStruct.toRequestDto(requestNew);
    }

    public List<RequestDto> getRequestsByUserId(Long userId) { //TODO реализовать с использованием пагинации
        List<Request> requests = requestRepository.getRequestsByRequestorIdOrderByCreatedDesc(userId);
        return requests.stream()
                .map(requestMapStruct::toRequestDto)
                .toList();
    }

    public List<RequestDto> getRequestsOtherUsers(Long userId) { //TODO реализовать с использованием пагинации
        List<Request> requests = requestRepository.getRequestsByIdNotOrderByCreatedDesc(userId);
        return requests.stream()
                .map(requestMapStruct::toRequestDto)
                .toList();
    }

    @Override
    public Request getRequestById(Long requestId) {
        return requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("запрос не найден"));
    }

    public RequestDto getRequestDtoById(Long userId, Long requestId) {
        Request request = requestRepository
                .findById(requestId).orElseThrow(() -> new NotFoundException("запрос не найден"));
        return requestMapStruct.toRequestDto(request);
    }

}