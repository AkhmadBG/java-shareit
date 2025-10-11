package ru.practicum.shareit.booking.service.imp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapStruct;
import ru.practicum.shareit.booking.dto.NewBookingAddRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.StateParam;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForRequest;
import ru.practicum.shareit.item.dto.ItemMapStruct;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.NewRequest;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapStruct;
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
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserService userService;

    @Mock
    private ItemService itemService;

    @Mock
    private BookingMapStruct bookingMapStruct;

    @Mock
    private UserMapStruct userMapStruct;

    @Mock
    private ItemMapStruct itemMapStruct;

    @InjectMocks
    private BookingServiceImpl bookingServiceImpl;

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

    }

    @Test
    void addBooking_ShouldCreateBookingAndReturnBookingDto() {
        Mockito.when(userService.getUserById(1L)).thenReturn(user);
        Mockito.when(itemService.getItemById(1L)).thenReturn(item);
        Mockito.when(userMapStruct.toUserDto(user)).thenReturn(userDto);
        Mockito.when(itemMapStruct.toItemDto(item)).thenReturn(itemDto);
        Mockito.when(bookingMapStruct.newBooking(userDto, itemDto, newBookingAddRequest)).thenReturn(booking);
        Mockito.when(bookingRepository.save(booking)).thenReturn(booking);
        Mockito.when(bookingMapStruct.toBookingDto(booking)).thenReturn(bookingDto);

        BookingDto result = bookingServiceImpl.addBooking(1L, newBookingAddRequest);

        assertThat(result).isEqualTo(bookingDto);
        verify(bookingMapStruct, times(1)).newBooking(userDto, itemDto, newBookingAddRequest);
        verify(bookingRepository, times(1)).save(booking);
        verify(bookingMapStruct, times(1)).toBookingDto(booking);
    }

    @Test
    void approvedBooking_ShouldSetStatusApprovedToBookingAndReturnBookingDto() {
        Mockito.when(bookingRepository.findByIdWithBookerAndItem(1L)).thenReturn(Optional.of(booking));
        Mockito.when(bookingRepository.save(booking)).thenReturn(booking);
        Mockito.when(bookingMapStruct.toBookingDto(booking)).thenReturn(bookingDto);

        BookingDto result = bookingServiceImpl.approvedBooking(1L, 1L, true);

        verify(bookingRepository, times(1)).findByIdWithBookerAndItem(1L);
        verify(bookingRepository, times(1)).save(booking);
        verify(bookingMapStruct, times(1)).toBookingDto(booking);
    }

    @Test
    void getBookingByBookerIdOrOwnerId_ShouldReturnBookingDtoByBookerIdOrOwnerId() {
        Mockito.when(bookingRepository.findByIdWithBookerAndItem(1L)).thenReturn(Optional.of(booking));
        Mockito.when(bookingMapStruct.toBookingDto(booking)).thenReturn(bookingDto);

        BookingDto result = bookingServiceImpl.getBookingByBookerIdOrOwnerId(1L, 1L);

        assertThat(result).isEqualTo(bookingDto);
        verify(bookingRepository, times(1)).findByIdWithBookerAndItem(1L);
        verify(bookingMapStruct, times(1)).toBookingDto(booking);
    }

    @Test
    void getUserBookings_ShouldReturnListBookingDto() {
        Mockito.when(bookingRepository.findByBookerIdAndStartBeforeAndEndAfter(
                eq(1L),
                any(LocalDateTime.class),
                any(LocalDateTime.class))
        ).thenReturn(List.of(booking));
        Mockito.when(bookingMapStruct.toBookingDto(booking)).thenReturn(bookingDto);

        List<BookingDto> result = bookingServiceImpl.getUserBookings(1L, stateParam);

        assertThat(result.getFirst()).isEqualTo(bookingDto);
        verify(bookingRepository, times(1)).findByBookerIdAndStartBeforeAndEndAfter(
                eq(1L),
                any(LocalDateTime.class),
                any(LocalDateTime.class));
        verify(bookingMapStruct, times(1)).toBookingDto(booking);
    }

    @Test
    void getBookingsForItemsByOwnerId_ShouldReturnListBookingDto() {
        Mockito.when(userService.getUserById(1L)).thenReturn(user);
        Mockito.when(bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(eq(1L), any(), any()))
                .thenReturn(List.of(booking));
        Mockito.when(bookingMapStruct.toBookingDto(booking)).thenReturn(bookingDto);

        List<BookingDto> result = bookingServiceImpl.getBookingsForItemsByOwnerId(1L, StateParam.CURRENT);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).isEqualTo(bookingDto);
        verify(bookingRepository).findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(eq(1L), any(), any());
    }

}