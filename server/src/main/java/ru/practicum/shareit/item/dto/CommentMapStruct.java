package ru.practicum.shareit.item.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CommentMapStruct {

    @Mapping(target = "authorName", source = "author.name")
    @Mapping(target = "itemId", source = "item.id")
    CommentDto toCommentDto(Comment comment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", source = "author")
    @Mapping(target = "item", source = "item")
    Comment newComment(User author, Item item, NewCommentAddRequest newCommentAddRequest);

}