package ru.practicum.shareit.item.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingAddRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.StateParam;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.NewRequest;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.service.RequestService;
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
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserService userService;

    @Mock
    private RequestService requestService;

    @Mock
    private ItemMapStruct itemMapStruct;

    @Mock
    private CommentMapStruct commentMapStruct;

    @InjectMocks
    private ItemServiceImpl itemServiceImpl;

    private Request request;
    private RequestDto requestDto;
    private NewRequest newRequest;
    private User user;
    private UserDto userDto;
    private Item item;
    private ItemDto itemDto;
    private ItemDtoForRequest itemDtoForRequest;
    private LocalDateTime localDateTime1 = LocalDateTime.now();
    private LocalDateTime localDateTime2 = LocalDateTime.now();
    private NewBookingAddRequest newBookingAddRequest;
    private Booking booking;
    private BookingDto bookingDto;
    private StateParam stateParam;
    private NewItemAddRequest newItemAddRequest;
    private UpdateItemRequest updateItemRequest;
    private Comment comment;
    private CommentDto commentDto;
    private NewCommentAddRequest newCommentAddRequest;

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

        itemDto = ItemDto.builder()
                .id(1L)
                .name("testItem")
                .description("testDescription")
                .available(true)
                .comments(List.of())
                .owner(userDto)
                .build();

        request = Request.builder()
                .id(1L)
                .description("testDescription")
                .created(localDateTime1)
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
                .created(localDateTime1)
                .description("testDescription")
                .items(List.of(itemDtoForRequest))
                .requestor(userDto)
                .build();

        newRequest = NewRequest.builder()
                .description("testNewRequest")
                .build();

        newBookingAddRequest = NewBookingAddRequest.builder()
                .start(localDateTime1)
                .end(localDateTime2)
                .itemId(1L)
                .build();

        booking = Booking.builder()
                .id(1L)
                .item(item)
                .booker(user)
                .start(localDateTime1)
                .end(localDateTime2)
                .status(BookingStatus.WAITING)
                .build();

        bookingDto = BookingDto.builder()
                .id(1L)
                .item(itemDto)
                .booker(userDto)
                .start(localDateTime1)
                .end(localDateTime2)
                .status(BookingStatus.WAITING)
                .build();

        stateParam = StateParam.CURRENT;

        newItemAddRequest = NewItemAddRequest.builder()
                .requestId(1L)
                .available(true)
                .description("Test")
                .name("Test")
                .build();

        updateItemRequest = UpdateItemRequest.builder()
                .available(true)
                .description("Test")
                .name("Test")
                .build();

        comment = Comment.builder()
                .id(1L)
                .text("Test")
                .author(user)
                .created(localDateTime1)
                .item(item)
                .build();

        commentDto = CommentDto.builder()
                .id(1L)
                .text("Test")
                .authorName(user.getName())
                .created(localDateTime1)
                .itemId(item.getId())
                .build();

        newCommentAddRequest = NewCommentAddRequest.builder()
                .text("Test")
                .build();

    }

    @Test
    void addItem_ShouldAddItemAndReturnItemDto() {
        Mockito.when(itemMapStruct.newItem(newItemAddRequest)).thenReturn(item);
        Mockito.when(userService.getUserById(1L)).thenReturn(user);
        Mockito.when(requestService.getRequestById(1L)).thenReturn(request);
        Mockito.when(itemRepository.save(item)).thenReturn(item);
        Mockito.when(itemMapStruct.toItemDto(item)).thenReturn(itemDto);

        ItemDto result = itemServiceImpl.addItem(1L, newItemAddRequest);

        assertThat(result).isEqualTo(itemDto);
        verify(itemMapStruct, times(1)).newItem(newItemAddRequest);
        verify(userService, times(1)).getUserById(1L);
        verify(requestService, times(1)).getRequestById(1L);
        verify(itemRepository, times(1)).save(item);
        verify(itemMapStruct, times(1)).toItemDto(item);
    }

    @Test
    void updateItem_ShouldAddItemAndReturnItemDto() {
        Mockito.when(itemRepository.findByIdWithOwnerAndRequest(1L)).thenReturn(Optional.of(item));
        Mockito.when(userService.getUserById(1L)).thenReturn(user);
        Mockito.doNothing().when(itemMapStruct).updateItem(item, updateItemRequest);
        Mockito.when(itemRepository.save(item)).thenReturn(item);
        Mockito.when(itemMapStruct.toItemDto(item)).thenReturn(itemDto);

        ItemDto result = itemServiceImpl.updateItem(1L, 1L, updateItemRequest);

        assertThat(result).isEqualTo(itemDto);
        verify(itemRepository, times(1)).findByIdWithOwnerAndRequest(1L);
        verify(userService, times(1)).getUserById(1L);
        verify(itemMapStruct, times(1)).updateItem(item, updateItemRequest);
        verify(itemRepository, times(1)).save(item);
        verify(itemMapStruct, times(1)).toItemDto(item);
    }

    @Test
    void getItemDtoWithDateById_ShouldReturnItemDtoWithBookings() {
        ItemDtoWithDate itemDtoWithDate = ItemDtoWithDate.builder()
                .id(1L)
                .name("testItem")
                .description("testDescription")
                .available(true)
                .build();

        Mockito.when(itemRepository.findByIdWithOwnerAndRequest(1L)).thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.findFirstByItemAndStartAfterOrderByStartAsc(eq(item), any(LocalDateTime.class)))
                .thenReturn(Optional.of(booking));
        Mockito.when(bookingRepository.findFirstByItemAndEndAfterOrderByEndDesc(eq(item), any(LocalDateTime.class)))
                .thenReturn(Optional.of(booking));
        Mockito.when(itemMapStruct.itemDtoWithDate(item)).thenReturn(itemDtoWithDate);

        ItemDtoWithDate result = itemServiceImpl.getItemDtoWithDateById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getNextBooking()).isEqualTo(booking.getStart());
        assertThat(result.getLastBooking()).isEqualTo(booking.getStart());
        verify(itemRepository, times(1)).findByIdWithOwnerAndRequest(1L);
        verify(bookingRepository, times(1))
                .findFirstByItemAndStartAfterOrderByStartAsc(eq(item), any(LocalDateTime.class));
        verify(itemMapStruct, times(1)).itemDtoWithDate(item);
    }

    @Test
    void getItemsByUserId_ShouldReturnListOfItemDto() {
        Mockito.when(itemRepository.findItemByOwnerId(1L)).thenReturn(List.of(item));
        Mockito.when(itemMapStruct.toItemDto(item)).thenReturn(itemDto);

        List<ItemDto> result = itemServiceImpl.getItemsByUserId(1L);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).isEqualTo(itemDto);
        verify(itemRepository, times(1)).findItemByOwnerId(1L);
        verify(itemMapStruct, times(1)).toItemDto(item);
    }

    @Test
    void deleteItem_ShouldInvokeRepositoryDelete() {
        itemServiceImpl.deleteItem(1L);
        verify(itemRepository, times(1)).deleteById(1L);
    }

    @Test
    void searchItemsByText_ShouldReturnListOfItems() {
        Mockito.when(itemRepository.searchItemsByText("test")).thenReturn(List.of(item));
        Mockito.when(itemMapStruct.toItemDto(item)).thenReturn(itemDto);

        List<ItemDto> result = itemServiceImpl.searchItemsByText("test");

        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).isEqualTo(itemDto);
        verify(itemRepository, times(1)).searchItemsByText("test");
        verify(itemMapStruct, times(1)).toItemDto(item);
    }

    @Test
    void searchItemsByText_ShouldReturnEmptyList_WhenTextBlank() {
        List<ItemDto> result = itemServiceImpl.searchItemsByText("   ");
        assertThat(result).isEmpty();
        verify(itemRepository, times(0)).searchItemsByText(any());
    }

    @Test
    void getItemById_ShouldReturnItem() {
        Mockito.when(itemRepository.findByIdWithOwnerAndRequest(1L)).thenReturn(Optional.of(item));

        Item result = itemServiceImpl.getItemById(1L);

        assertThat(result).isEqualTo(item);
        verify(itemRepository, times(1)).findByIdWithOwnerAndRequest(1L);
    }

    @Test
    void addComment_ShouldAddAndReturnCommentDto() {
        Booking pastBooking = Booking.builder()
                .id(1L)
                .item(item)
                .booker(user)
                .end(LocalDateTime.now().minusDays(1))
                .status(BookingStatus.APPROVED)
                .build();

        Mockito.when(userService.getUserById(1L)).thenReturn(user);
        Mockito.when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        Mockito.when(bookingRepository.findFirstByBookerAndItemAndEndBeforeOrderByEndDesc(
                        eq(user), eq(item), any(LocalDateTime.class)))
                .thenReturn(Optional.of(pastBooking));
        Mockito.when(commentMapStruct.newComment(user, item, newCommentAddRequest)).thenReturn(comment);
        Mockito.when(commentRepository.save(comment)).thenReturn(comment);
        Mockito.when(commentMapStruct.toCommentDto(comment)).thenReturn(commentDto);

        CommentDto result = itemServiceImpl.addComment(1L, 1L, newCommentAddRequest);

        assertThat(result).isEqualTo(commentDto);
        verify(commentRepository, times(1)).save(comment);
        verify(commentMapStruct, times(1)).toCommentDto(comment);
    }

}