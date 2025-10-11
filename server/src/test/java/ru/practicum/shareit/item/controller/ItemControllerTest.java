package ru.practicum.shareit.item.controller;

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
import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.shareit.util.AppConstant.CUSTOM_REQUEST_HEADER_USER_ID;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @Autowired
    private ObjectMapper objectMapper;
    private ItemDto itemDto;
    private ItemDtoWithDate itemDtoWithDate;
    private CommentDto commentDto;
    private NewItemAddRequest newItemAddRequest;
    private UpdateItemRequest updateItemRequest;
    private NewCommentAddRequest newCommentAddRequest;

    @BeforeEach
    void setup() {

        itemDto = ItemDto.builder()
                .id(1L)
                .name("TestItem")
                .description("TestDesc")
                .available(true)
                .build();

        itemDtoWithDate = ItemDtoWithDate.builder()
                .id(1L)
                .name("TestItem")
                .description("TestDesc")
                .available(true)
                .lastBooking(null)
                .nextBooking(null)
                .build();

        commentDto = CommentDto.builder()
                .id(1L)
                .text("Test comment")
                .authorName("TestUser")
                .created(LocalDateTime.now())
                .itemId(1L)
                .build();

        newItemAddRequest = NewItemAddRequest.builder()
                .name("TestItem")
                .description("TestDesc")
                .available(true)
                .build();

        updateItemRequest = UpdateItemRequest.builder()
                .name("UpdatedItem")
                .description("UpdatedDesc")
                .available(false)
                .build();

        newCommentAddRequest = NewCommentAddRequest.builder()
                .text("Test comment")
                .build();

    }

    @Test
    void addItem_ShouldReturnCreatedItem() throws Exception {
        Mockito.when(itemService.addItem(eq(1L), any(NewItemAddRequest.class)))
                .thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(CUSTOM_REQUEST_HEADER_USER_ID, 1L)
                        .content(objectMapper.writeValueAsString(newItemAddRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()));
    }

    @Test
    void updateItem_ShouldReturnUpdatedItem() throws Exception {
        Mockito.when(itemService.updateItem(eq(1L), eq(1L), any(UpdateItemRequest.class)))
                .thenReturn(itemDto);

        mockMvc.perform(patch("/items/1")
                        .header(CUSTOM_REQUEST_HEADER_USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateItemRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()));
    }

    @Test
    void getItemById_ShouldReturnItemWithDate() throws Exception {
        Mockito.when(itemService.getItemDtoWithDateById(1L))
                .thenReturn(itemDtoWithDate);

        mockMvc.perform(get("/items/1")
                        .header(CUSTOM_REQUEST_HEADER_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDtoWithDate.getId()))
                .andExpect(jsonPath("$.name").value(itemDtoWithDate.getName()));
    }

    @Test
    void getItemsByUserId_ShouldReturnItemList() throws Exception {
        Mockito.when(itemService.getItemsByUserId(1L))
                .thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items")
                        .header(CUSTOM_REQUEST_HEADER_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemDto.getId()))
                .andExpect(jsonPath("$[0].name").value(itemDto.getName()));
    }

    @Test
    void deleteItem_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/items/1")
                        .header(CUSTOM_REQUEST_HEADER_USER_ID, 1L))
                .andExpect(status().isNoContent());
        Mockito.verify(itemService).deleteItem(1L);
    }

    @Test
    void searchItems_ShouldReturnItemList() throws Exception {
        Mockito.when(itemService.searchItemsByText("Test"))
                .thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items/search")
                        .param("text", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemDto.getId()))
                .andExpect(jsonPath("$[0].name").value(itemDto.getName()));
    }

    @Test
    void addComment_ShouldReturnCreatedComment() throws Exception {
        Mockito.when(itemService.addComment(eq(1L), eq(1L), any(NewCommentAddRequest.class)))
                .thenReturn(commentDto);

        mockMvc.perform(post("/items/1/comment")
                        .header(CUSTOM_REQUEST_HEADER_USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCommentAddRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(commentDto.getId()))
                .andExpect(jsonPath("$.text").value(commentDto.getText()));
    }

    @Test
    void addItem_ShouldReturnInternalServerError_WhenUnexpectedError() throws Exception {
        Mockito.when(itemService.addItem(eq(1L), any(NewItemAddRequest.class)))
                .thenThrow(new RuntimeException("ошибка при создании вещи"));

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(CUSTOM_REQUEST_HEADER_USER_ID, 1L)
                        .content(objectMapper.writeValueAsString(newItemAddRequest)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void updateItem_ShouldReturnForbidden_WhenUserNotOwner() throws Exception {
        Mockito.when(itemService.updateItem(eq(1L), eq(1L), any(UpdateItemRequest.class)))
                .thenThrow(new AccessException("редактировать вещь может только владелец"));

        mockMvc.perform(patch("/items/1")
                        .header(CUSTOM_REQUEST_HEADER_USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateItemRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    void getItemById_ShouldReturnNotFound_WhenItemMissing() throws Exception {
        Mockito.when(itemService.getItemDtoWithDateById(1L))
                .thenThrow(new NotFoundException("вещь не найдена"));

        mockMvc.perform(get("/items/1")
                        .header(CUSTOM_REQUEST_HEADER_USER_ID, 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void getItemsByUserId_ShouldReturnInternalServerError_WhenErrorOccurs() throws Exception {
        Mockito.when(itemService.getItemsByUserId(1L))
                .thenThrow(new RuntimeException("ошибка получения списка вещей"));

        mockMvc.perform(get("/items")
                        .header(CUSTOM_REQUEST_HEADER_USER_ID, 1L))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void deleteItem_ShouldReturnNotFound_WhenItemDoesNotExist() throws Exception {
        Mockito.doThrow(new NotFoundException("вещь для удаления не найдена"))
                .when(itemService).deleteItem(1L);

        mockMvc.perform(delete("/items/1")
                        .header(CUSTOM_REQUEST_HEADER_USER_ID, 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void searchItems_ShouldReturnInternalError_WhenServiceFails() throws Exception {
        Mockito.when(itemService.searchItemsByText("fail"))
                .thenThrow(new RuntimeException("ошибка поиска"));

        mockMvc.perform(get("/items/search")
                        .param("text", "fail"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void addComment_ShouldReturnForbidden_WhenUserCannotComment() throws Exception {
        Mockito.when(itemService.addComment(eq(1L), eq(1L), any(NewCommentAddRequest.class)))
                .thenThrow(new AccessException("пользователь не может оставить комментарий"));

        mockMvc.perform(post("/items/1/comment")
                        .header(CUSTOM_REQUEST_HEADER_USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCommentAddRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    void addComment_ShouldReturnNotFound_WhenItemMissing() throws Exception {
        Mockito.when(itemService.addComment(eq(1L), eq(1L), any(NewCommentAddRequest.class)))
                .thenThrow(new NotFoundException("вещь не найдена"));

        mockMvc.perform(post("/items/1/comment")
                        .header(CUSTOM_REQUEST_HEADER_USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCommentAddRequest)))
                .andExpect(status().isNotFound());
    }

}