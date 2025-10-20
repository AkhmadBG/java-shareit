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

    @Override
    public RequestDto addRequest(Long userId, NewRequest newRequest) {
        log.info("RequestServiceImpl: Добавление нового запроса пользователем userId={}, запрос={}", userId, newRequest);
        User requestor = userService.getUserById(userId);
        Request request = requestMapStruct.newRequest(requestor, newRequest, LocalDateTime.now());
        Request requestNew = requestRepository.save(request);
        log.info("RequestServiceImpl: Запрос успешно добавлен: requestId={}, userId={}", requestNew.getId(), userId);
        return requestMapStruct.toRequestDto(requestNew);
    }

    @Override
    public List<RequestDto> getRequestsByUserId(Long userId) { // TODO: реализовать с пагинацией
        log.info("RequestServiceImpl: Получение списка запросов пользователя userId={}", userId);
        List<Request> requests = requestRepository.getRequestsByRequestorIdOrderByCreatedDesc(userId);
        log.info("RequestServiceImpl: Найдено {} запросов пользователя userId={}", requests.size(), userId);
        return requests.stream()
                .map(requestMapStruct::toRequestDto)
                .toList();
    }

    @Override
    public List<RequestDto> getRequestsOtherUsers(Long userId) { // TODO: реализовать с пагинацией
        log.info("RequestServiceImpl: Получение запросов других пользователей для userId={}", userId);
        List<Request> requests = requestRepository.getRequestsByIdNotOrderByCreatedDesc(userId);
        log.info("RequestServiceImpl: Найдено {} запросов других пользователей для userId={}", requests.size(), userId);
        return requests.stream()
                .map(requestMapStruct::toRequestDto)
                .toList();
    }

    @Override
    public Request getRequestById(Long requestId) {
        log.info("RequestServiceImpl: Поиск запроса по id={}", requestId);
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> {
                    log.warn("RequestServiceImpl: Запрос с id={} не найден", requestId);
                    return new NotFoundException("запрос не найден");
                });
        log.info("RequestServiceImpl: Запрос найден: requestId={}", request.getId());
        return request;
    }

    @Override
    public RequestDto getRequestDtoById(Long userId, Long requestId) {
        log.info("RequestServiceImpl: Получение DTO запроса: userId={}, requestId={}", userId, requestId);
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> {
                    log.warn("RequestServiceImpl: Запрос с id={} не найден (userId={})", requestId, userId);
                    return new NotFoundException("запрос не найден");
                });
        RequestDto dto = requestMapStruct.toRequestDto(request);
        log.info("RequestServiceImpl: DTO запроса успешно получен: requestId={}, userId={}", requestId, userId);
        return dto;
    }

}