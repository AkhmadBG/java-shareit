package ru.practicum.shareit.request.service.impl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDtoForRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.NewRequest;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestMapStruct;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RequestServiceImplTest {

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private UserService userService;

    @Mock
    private RequestMapStruct requestMapStruct;

    @InjectMocks
    private RequestServiceImpl requestServiceImpl;

    private Request request;
    private RequestDto requestDto;
    private NewRequest newRequest;
    private User user;
    private UserDto userDto;
    private Item item;
    private ItemDtoForRequest itemDtoForRequest;
    private LocalDateTime localDateTime = LocalDateTime.now();

    @BeforeEach
    void setup() {

        user = User.builder()
                .id(1L)
                .name("Test")
                .email("test@test.ru")
                .build();

        userDto = UserDto.builder()
                .id(1L)
                .name("Test")
                .email("test@test.ru")
                .build();

        item = Item.builder()
                .id(1L)
                .name("testItem")
                .description("testDescription")
                .available(true)
                .comments(List.of())
                .owner(user)
                .build();

        request = Request.builder()
                .id(1L)
                .description("testDescription")
                .created(localDateTime)
                .items(List.of(item))
                .requestor(user)
                .build();

        itemDtoForRequest = ItemDtoForRequest.builder()
                .name("test")
                .itemId(1L)
                .ownerId(1L)
                .build();

        requestDto = RequestDto.builder()
                .id(1L)
                .created(localDateTime)
                .description("testDescription")
                .items(List.of(itemDtoForRequest))
                .requestor(userDto)
                .build();

        newRequest = NewRequest.builder()
                .description("testNewRequest")
                .build();

    }

    @Test
    void addRequest_ShouldCreateRequestAndReturnRequestDto() {
        Mockito.when(userService.getUserById(1L)).thenReturn(user);
        Mockito.when(requestMapStruct.newRequest(eq(user), eq(newRequest), any(LocalDateTime.class)))
                .thenReturn(request);
        Mockito.when(requestRepository.save(request)).thenReturn(request);
        Mockito.when(requestMapStruct.toRequestDto(request)).thenReturn(requestDto);

        RequestDto result = requestServiceImpl.addRequest(1L, newRequest);

        assertThat(result).isEqualTo(requestDto);
        verify(userService, times(1)).getUserById(1L);
        verify(requestMapStruct, times(1))
                .newRequest(eq(user), eq(newRequest), any(LocalDateTime.class));
        verify(requestRepository, times(1)).save(request);
        verify(requestMapStruct, times(1)).toRequestDto(request);
    }

    @Test
    void getRequestsByUserId_ShouldReturnRequestsByUserId() {
        Mockito.when(requestRepository.getRequestsByRequestorIdOrderByCreatedDesc(1L)).thenReturn(List.of(request));
        Mockito.when(requestMapStruct.toRequestDto(request)).thenReturn(requestDto);

        List<RequestDto> result = requestServiceImpl.getRequestsByUserId(1L);

        assertThat(result).containsExactly(requestDto);
        verify(requestRepository, times(1)).getRequestsByRequestorIdOrderByCreatedDesc(1L);
        verify(requestMapStruct, times(1)).toRequestDto(request);
    }

    @Test
    void getRequestsOtherUsers_ShouldReturnRequests() {
        Mockito.when(requestRepository.getRequestsByIdNotOrderByCreatedDesc(1L)).thenReturn(List.of(request));
        Mockito.when(requestMapStruct.toRequestDto(request)).thenReturn(requestDto);

        List<RequestDto> result = requestServiceImpl.getRequestsOtherUsers(1L);

        assertThat(result).containsExactly(requestDto);
        verify(requestRepository, times(1)).getRequestsByIdNotOrderByCreatedDesc(1L);
        verify(requestMapStruct, times(1)).toRequestDto(request);
    }

    @Test
    void getRequestById_ShouldReturnRequestById() {
        Mockito.when(requestRepository.findById(1L)).thenReturn(Optional.of(request));

        Request result = requestServiceImpl.getRequestById(1L);

        assertThat(result).isEqualTo(result);
        verify(requestRepository, times(1)).findById(1L);
    }

    @Test
    void getRequestDtoById_ShouldReturnRequestDtoById() {
        Mockito.when(requestRepository.findById(1L)).thenReturn(Optional.of(request));
        Mockito.when(requestMapStruct.toRequestDto(request)).thenReturn(requestDto);

        RequestDto result = requestServiceImpl.getRequestDtoById(1L, 1L);

        assertThat(result).isEqualTo(requestDto);
        verify(requestRepository, times(1)).findById(1L);
        verify(requestMapStruct, times(1)).toRequestDto(request);
    }

    @Test
    void getRequestById_ShouldThrowNotFoundException_WhenRequestNotFound() {
        Mockito.when(requestRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> requestServiceImpl.getRequestById(1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getRequestDtoById_ShouldThrowNotFoundException_WhenRequestNotFound() {
        Mockito.when(requestRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> requestServiceImpl.getRequestDtoById(1L, 1L))
                .isInstanceOf(NotFoundException.class);
    }

}