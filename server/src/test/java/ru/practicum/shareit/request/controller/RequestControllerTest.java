package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDtoForRequest;
import ru.practicum.shareit.request.dto.NewRequest;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.shareit.util.AppConstant.CUSTOM_REQUEST_HEADER_USER_ID;

@ExtendWith(SpringExtension.class)
@WebMvcTest(RequestController.class)
class RequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RequestService requestService;

    @Autowired
    private ObjectMapper objectMapper;

    private RequestDto requestDto;
    private NewRequest newRequest;
    private ItemDtoForRequest itemDtoForRequest;
    private UserDto userDto;
    private LocalDateTime localDateTime = LocalDateTime.now();

    @BeforeEach
    void setup() {

        userDto = UserDto.builder()
                .id(1L)
                .name("TestUser")
                .email("test@test.ru")
                .build();

        itemDtoForRequest = ItemDtoForRequest.builder()
                .itemId(1L)
                .name("TestItem")
                .ownerId(1L)
                .build();

        requestDto = RequestDto.builder()
                .id(1L)
                .description("TestDescription")
                .created(localDateTime)
                .items(List.of(itemDtoForRequest))
                .requestor(userDto)
                .build();

        newRequest = NewRequest.builder()
                .description("New test request")
                .build();

    }

    @Test
    void addRequest_ShouldReturnCreatedRequest() throws Exception {
        Mockito.when(requestService.addRequest(eq(1L), any(NewRequest.class)))
                .thenReturn(requestDto);

        mockMvc.perform(post("/requests")
                        .header(CUSTOM_REQUEST_HEADER_USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestDto.getId()))
                .andExpect(jsonPath("$.description").value(requestDto.getDescription()))
                .andExpect(jsonPath("$.requestor.id").value(userDto.getId()))
                .andExpect(jsonPath("$.items[0].itemId").value(itemDtoForRequest.getItemId()));

        Mockito.verify(requestService).addRequest(eq(1L), any(NewRequest.class));
    }

    @Test
    void getRequestsByUserId_ShouldReturnUserRequests() throws Exception {
        Mockito.when(requestService.getRequestsByUserId(1L))
                .thenReturn(List.of(requestDto));

        mockMvc.perform(get("/requests")
                        .header(CUSTOM_REQUEST_HEADER_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(requestDto.getId()))
                .andExpect(jsonPath("$[0].description").value(requestDto.getDescription()));

        Mockito.verify(requestService).getRequestsByUserId(1L);
    }

    @Test
    void getRequestsOtherUsers_ShouldReturnOtherUsersRequests() throws Exception {
        Mockito.when(requestService.getRequestsOtherUsers(1L))
                .thenReturn(List.of(requestDto));

        mockMvc.perform(get("/requests/all")
                        .header(CUSTOM_REQUEST_HEADER_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(requestDto.getId()))
                .andExpect(jsonPath("$[0].description").value(requestDto.getDescription()));

        Mockito.verify(requestService).getRequestsOtherUsers(1L);
    }

    @Test
    void getRequestById_ShouldReturnRequest() throws Exception {
        Mockito.when(requestService.getRequestDtoById(1L, 1L))
                .thenReturn(requestDto);

        mockMvc.perform(get("/requests/1")
                        .header(CUSTOM_REQUEST_HEADER_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestDto.getId()))
                .andExpect(jsonPath("$.description").value(requestDto.getDescription()))
                .andExpect(jsonPath("$.requestor.id").value(userDto.getId()))
                .andExpect(jsonPath("$.items[0].itemId").value(itemDtoForRequest.getItemId()));

        Mockito.verify(requestService).getRequestDtoById(1L, 1L);
    }

    @Test
    void getRequestById_ShouldReturnNotFound_WhenRequestDoesNotExist() throws Exception {
        Mockito.when(requestService.getRequestDtoById(1L, 1L))
                .thenThrow(new NotFoundException("Заявка не найдена"));

        mockMvc.perform(get("/requests/1")
                        .header(CUSTOM_REQUEST_HEADER_USER_ID, 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void getRequestsByUserId_ShouldReturnEmptyList_WhenUserHasNoRequests() throws Exception {
        Mockito.when(requestService.getRequestsByUserId(1L))
                .thenThrow(new NotFoundException("Заявки пользователя не найдены"));

        mockMvc.perform(get("/requests")
                        .header(CUSTOM_REQUEST_HEADER_USER_ID, 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void getRequestsOtherUsers_ShouldReturnEmptyList_WhenNoOtherUsersRequests() throws Exception {
        Mockito.when(requestService.getRequestsOtherUsers(1L))
                .thenThrow(new NotFoundException("Заявки других пользователей не найдены"));

        mockMvc.perform(get("/requests/all")
                        .header(CUSTOM_REQUEST_HEADER_USER_ID, 1L))
                .andExpect(status().isNotFound());
    }

}