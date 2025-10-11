package ru.practicum.shareit.user.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.user.dto.NewUserAddRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapStruct;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserMapStruct userMapStruct;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    private User user;
    private UserDto userDto;
    private NewUserAddRequest newUserAddRequest;
    private UpdateUserRequest updateUserRequest;

    @BeforeEach
    void setUp() {

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
        newUserAddRequest = NewUserAddRequest.builder()
                .name("Test")
                .email("test@test.ru")
                .build();
        updateUserRequest = UpdateUserRequest.builder()
                .name("UpdateTest")
                .email("updatetest@test.ru")
                .build();

    }

    @Test
    void addUser_ShouldCreateUserAndReturnUserDto() {
        Mockito.when(userMapStruct.newUser(newUserAddRequest)).thenReturn(user);
        Mockito.when(userRepository.save(user)).thenReturn(user);
        Mockito.when(userMapStruct.toUserDto(user)).thenReturn(userDto);

        UserDto result = userServiceImpl.addUser(newUserAddRequest);

        assertThat(result).isEqualTo(userDto);
        verify(userRepository, times(1)).save(user);
        verify(userMapStruct, times(1)).newUser(newUserAddRequest);
    }

    @Test
    void updateUserDto() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(userMapStruct.toUserDto(user)).thenReturn(userDto);

        UserDto result = userServiceImpl.updateUserDto(user.getId(), updateUserRequest);

        assertThat(result).isEqualTo(userDto);
        verify(userRepository, times(1)).findById(1L);
        verify(userMapStruct, times(1)).toUserDto(user);
    }

    @Test
    void getUserDtoById_ShouldReturnUserDto() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(userMapStruct.toUserDto(user)).thenReturn(userDto);

        UserDto result = userServiceImpl.getUserDtoById(1L);

        assertThat(result).isEqualTo(userDto);
        verify(userRepository, times(1)).findById(1L);
        verify(userMapStruct, times(1)).toUserDto(user);
    }

    @Test
    void getAllUsersDto_ShouldReturnPageUsers() {
        Page<User> userPage = new PageImpl<>(List.of(user));
        Mockito.when(userRepository.findAll(PageRequest.of(0, 10))).thenReturn(userPage);
        Mockito.when(userMapStruct.toUserDto(user)).thenReturn(userDto);

        Page<UserDto> result = userServiceImpl.getAllUsersDto(0, 10);

        assertThat(result.getContent()).containsExactly(userDto);
        verify(userRepository).findAll(PageRequest.of(0, 10));
    }

    @Test
    void deleteUser_ShouldDeleteUser() {
        Mockito.doNothing().when(userRepository).deleteById(1L);

        userServiceImpl.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void getUserById_ShouldReturnUser() {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userServiceImpl.getUserById(1L);

        assertThat(result).isEqualTo(user);
    }

}