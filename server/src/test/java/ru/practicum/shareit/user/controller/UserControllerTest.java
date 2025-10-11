package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.NewUserAddRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private UserDto userDto;
    private NewUserAddRequest newUserAddRequest;
    private UpdateUserRequest updateUserRequest;

    @BeforeEach
    void setup() {

        userDto = UserDto.builder()
                .id(1L)
                .name("Test User")
                .email("test@test.com")
                .build();

        newUserAddRequest = NewUserAddRequest.builder()
                .name("Test User")
                .email("test@test.com")
                .build();

        updateUserRequest = UpdateUserRequest.builder()
                .name("Updated Name")
                .email("updated@test.com")
                .build();

    }

    @Test
    void addUser_ShouldReturnCreatedUser() throws Exception {
        Mockito.when(userService.addUser(any(NewUserAddRequest.class))).thenReturn(userDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUserAddRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));

        Mockito.verify(userService).addUser(any(NewUserAddRequest.class));
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser() throws Exception {
        Mockito.when(userService.updateUserDto(eq(1L), any(UpdateUserRequest.class))).thenReturn(userDto);

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));

        Mockito.verify(userService).updateUserDto(eq(1L), any(UpdateUserRequest.class));
    }

    @Test
    void getUserById_ShouldReturnUser() throws Exception {
        Mockito.when(userService.getUserDtoById(1L)).thenReturn(userDto);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));

        Mockito.verify(userService).getUserDtoById(1L);
    }

    @Test
    void getAllUsers_ShouldReturnPagedUsers() throws Exception {
        Mockito.when(userService.getAllUsersDto(0, 10))
                .thenReturn(new PageImpl<>(List.of(userDto)));

        mockMvc.perform(get("/users")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(userDto.getId()))
                .andExpect(jsonPath("$.content[0].name").value(userDto.getName()))
                .andExpect(jsonPath("$.content[0].email").value(userDto.getEmail()));

        Mockito.verify(userService).getAllUsersDto(0, 10);
    }

    @Test
    void deleteUser_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());

        Mockito.verify(userService).deleteUser(1L);
    }

}