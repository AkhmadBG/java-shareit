package ru.practicum.shareit.user.dto;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapStruct {

    UserDto toUserDto(User user);

    User newUser(NewUserAddRequest newUserAddRequest);

    void updateUser(@MappingTarget User user, UpdateUserRequest updateUserRequest);

}