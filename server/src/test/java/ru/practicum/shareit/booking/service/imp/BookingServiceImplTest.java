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
import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.exception.NotFoundException;
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
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
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
    void getUserBookings_ShouldReturnListBookingDtoWithStateParamCURRENT() {
        Mockito.when(bookingRepository.findByBookerIdAndStartBeforeAndEndAfter(
                eq(1L),
                any(LocalDateTime.class),
                any(LocalDateTime.class))
        ).thenReturn(List.of(booking));
        Mockito.when(bookingMapStruct.toBookingDto(booking)).thenReturn(bookingDto);

        List<BookingDto> result = bookingServiceImpl.getUserBookings(1L, StateParam.CURRENT);

        assertThat(result.getFirst()).isEqualTo(bookingDto);
        verify(bookingRepository, times(1)).findByBookerIdAndStartBeforeAndEndAfter(
                eq(1L),
                any(LocalDateTime.class),
                any(LocalDateTime.class));
        verify(bookingMapStruct, times(1)).toBookingDto(booking);
    }

    @Test
    void getUserBookings_ShouldReturnListBookingDtoWithStateParamPast() {
        Mockito.when(bookingRepository.findByBookerIdAndEndBefore(
                eq(1L),
                any(LocalDateTime.class))
        ).thenReturn(List.of(booking));
        Mockito.when(bookingMapStruct.toBookingDto(booking)).thenReturn(bookingDto);

        List<BookingDto> result = bookingServiceImpl.getUserBookings(1L, StateParam.PAST);

        assertThat(result.getFirst()).isEqualTo(bookingDto);
        verify(bookingRepository, times(1)).findByBookerIdAndEndBefore(
                eq(1L),
                any(LocalDateTime.class));
        verify(bookingMapStruct, times(1)).toBookingDto(booking);
    }

    @Test
    void getUserBookings_ShouldReturnListBookingDtoWithStateParamFUTURE() {
        Mockito.when(bookingRepository.findByBookerIdAndStartAfter(
                eq(1L),
                any(LocalDateTime.class))
        ).thenReturn(List.of(booking));
        Mockito.when(bookingMapStruct.toBookingDto(booking)).thenReturn(bookingDto);

        List<BookingDto> result = bookingServiceImpl.getUserBookings(1L, StateParam.FUTURE);

        assertThat(result.getFirst()).isEqualTo(bookingDto);
        verify(bookingRepository, times(1)).findByBookerIdAndStartAfter(
                eq(1L),
                any(LocalDateTime.class));
        verify(bookingMapStruct, times(1)).toBookingDto(booking);
    }

    @Test
    void getUserBookings_ShouldReturnListBookingDtoWithStateParamWAITING() {
        Mockito.when(bookingRepository.findByBookerIdAndStatus(1L, BookingStatus.WAITING)
        ).thenReturn(List.of(booking));
        Mockito.when(bookingMapStruct.toBookingDto(booking)).thenReturn(bookingDto);

        List<BookingDto> result = bookingServiceImpl.getUserBookings(1L, StateParam.WAITING);

        assertThat(result.getFirst()).isEqualTo(bookingDto);
        verify(bookingRepository, times(1)).findByBookerIdAndStatus(1L, BookingStatus.WAITING);
        verify(bookingMapStruct, times(1)).toBookingDto(booking);
    }

    @Test
    void getUserBookings_ShouldReturnListBookingDtoWithStateParamREJECTED() {
        Mockito.when(bookingRepository.findByBookerIdAndStatus(1L, BookingStatus.REJECTED)
        ).thenReturn(List.of(booking));
        Mockito.when(bookingMapStruct.toBookingDto(booking)).thenReturn(bookingDto);

        List<BookingDto> result = bookingServiceImpl.getUserBookings(1L, StateParam.REJECTED);

        assertThat(result.getFirst()).isEqualTo(bookingDto);
        verify(bookingRepository, times(1)).findByBookerIdAndStatus(1L, BookingStatus.REJECTED);
        verify(bookingMapStruct, times(1)).toBookingDto(booking);
    }

    @Test
    void getBookingsForItemsByOwnerId_ShouldReturnListBookingDtoWithStateParamCURRENT() {
        Mockito.when(userService.getUserById(1L)).thenReturn(user);
        Mockito.when(bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(eq(1L), any(), any()))
                .thenReturn(List.of(booking));
        Mockito.when(bookingMapStruct.toBookingDto(booking)).thenReturn(bookingDto);

        List<BookingDto> result = bookingServiceImpl.getBookingsForItemsByOwnerId(1L, StateParam.CURRENT);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).isEqualTo(bookingDto);
        verify(bookingRepository).findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(eq(1L), any(), any());
    }

    @Test
    void getBookingsForItemsByOwnerId_ShouldReturnListBookingDtoWithStateParamPAST() {
        Mockito.when(userService.getUserById(1L)).thenReturn(user);
        Mockito.when(bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(eq(1L), any()))
                .thenReturn(List.of(booking));
        Mockito.when(bookingMapStruct.toBookingDto(booking)).thenReturn(bookingDto);

        List<BookingDto> result = bookingServiceImpl.getBookingsForItemsByOwnerId(1L, StateParam.PAST);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).isEqualTo(bookingDto);
        verify(bookingRepository).findByItemOwnerIdAndEndBeforeOrderByStartDesc(eq(1L), any());
    }

    @Test
    void getBookingsForItemsByOwnerId_ShouldReturnListBookingDtoWithStateParamFUTURE() {
        Mockito.when(userService.getUserById(1L)).thenReturn(user);
        Mockito.when(bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(eq(1L), any()))
                .thenReturn(List.of(booking));
        Mockito.when(bookingMapStruct.toBookingDto(booking)).thenReturn(bookingDto);

        List<BookingDto> result = bookingServiceImpl.getBookingsForItemsByOwnerId(1L, StateParam.FUTURE);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).isEqualTo(bookingDto);
        verify(bookingRepository).findByItemOwnerIdAndStartAfterOrderByStartDesc(eq(1L), any());
    }

    @Test
    void getBookingsForItemsByOwnerId_ShouldReturnListBookingDtoWithStateParamWAITING() {
        Mockito.when(userService.getUserById(1L)).thenReturn(user);
        Mockito.when(bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(1L, BookingStatus.WAITING))
                .thenReturn(List.of(booking));
        Mockito.when(bookingMapStruct.toBookingDto(booking)).thenReturn(bookingDto);

        List<BookingDto> result = bookingServiceImpl.getBookingsForItemsByOwnerId(1L, StateParam.WAITING);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).isEqualTo(bookingDto);
        verify(bookingRepository).findByItemOwnerIdAndStatusOrderByStartDesc(1L, BookingStatus.WAITING);
    }

    @Test
    void getBookingsForItemsByOwnerId_ShouldReturnListBookingDtoWithStateParamREJECTED() {
        Mockito.when(userService.getUserById(1L)).thenReturn(user);
        Mockito.when(bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(1L, BookingStatus.REJECTED))
                .thenReturn(List.of(booking));
        Mockito.when(bookingMapStruct.toBookingDto(booking)).thenReturn(bookingDto);

        List<BookingDto> result = bookingServiceImpl.getBookingsForItemsByOwnerId(1L, StateParam.REJECTED);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).isEqualTo(bookingDto);
        verify(bookingRepository).findByItemOwnerIdAndStatusOrderByStartDesc(1L, BookingStatus.REJECTED);
    }

    @Test
    void approvedBooking_ShouldThrowNotFoundException_WhenBookingDoesNotExist() {
        Mockito.when(bookingRepository.findByIdWithBookerAndItem(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingServiceImpl.approvedBooking(1L, 1L, true))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void approvedBooking_ShouldThrowAccessException_WhenUserIsNotOwner() {
        User otherUser = User.builder().id(2L).build();
        item.setOwner(otherUser);
        Mockito.when(bookingRepository.findByIdWithBookerAndItem(1L)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingServiceImpl.approvedBooking(1L, 1L, true))
                .isInstanceOf(AccessException.class);
    }

    @Test
    void getBookingByBookerIdOrOwnerId_ShouldThrowNotFoundException_WhenBookingDoesNotExist() {
        Mockito.when(bookingRepository.findByIdWithBookerAndItem(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingServiceImpl.getBookingByBookerIdOrOwnerId(1L, 1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getBookingByBookerIdOrOwnerId_ShouldThrowAccessException_WhenUserHasNoRights() {
        User otherUser = User.builder().id(2L).build();
        item.setOwner(otherUser);
        user.setId(3L);
        booking.setBooker(user);
        Mockito.when(bookingRepository.findByIdWithBookerAndItem(1L)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingServiceImpl.getBookingByBookerIdOrOwnerId(4L, 1L))
                .isInstanceOf(AccessException.class);
    }

}