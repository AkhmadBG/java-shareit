package ru.practicum.shareit.booking.controller;

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
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingAddRequest;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.StateParam;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.shareit.util.AppConstant.CUSTOM_REQUEST_HEADER_USER_ID;

@ExtendWith(SpringExtension.class)
@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper objectMapper;

    private NewBookingAddRequest newBookingAddRequest;
    private BookingDto bookingDto;
    private LocalDateTime localDateTime1 = LocalDateTime.now();
    private LocalDateTime localDateTime2 = LocalDateTime.now();
    private ItemDto itemDto;
    private UserDto userDto;


    @BeforeEach
    void setup() {

        userDto = UserDto.builder()
                .id(1L)
                .name("Test")
                .email("test@test.ru")
                .build();

        itemDto = ItemDto.builder()
                .id(1L)
                .name("testItem")
                .description("testDescription")
                .available(true)
                .comments(List.of())
                .owner(userDto)
                .build();

        newBookingAddRequest = NewBookingAddRequest.builder()
                .start(localDateTime1)
                .end(localDateTime2)
                .itemId(1L)
                .build();

        bookingDto = BookingDto.builder()
                .id(1L)
                .item(itemDto)
                .booker(userDto)
                .start(localDateTime1)
                .end(localDateTime2)
                .status(BookingStatus.WAITING)
                .build();

    }

    @Test
    void addBooking_ShouldCreateBookingAndReturnBookingDto() throws Exception {
        Mockito.when(bookingService.addBooking(eq(1L), any(NewBookingAddRequest.class)))
                .thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(CUSTOM_REQUEST_HEADER_USER_ID, 1L)
                        .content(objectMapper.writeValueAsString(newBookingAddRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()))
                .andExpect(jsonPath("$.item").value(itemDto))
                .andExpect(jsonPath("$.booker").value(userDto));
    }

    @Test
    void approvedBooking_ShouldReturnBookingDtoWithApprovedStatus() throws Exception {
        BookingDto approvedBooking = BookingDto.builder()
                .id(1L)
                .item(itemDto)
                .booker(userDto)
                .start(localDateTime1)
                .end(localDateTime2)
                .status(BookingStatus.APPROVED)
                .build();

        Mockito.when(bookingService.approvedBooking(1L, 1L, true))
                .thenReturn(approvedBooking);

        mockMvc.perform(patch("/bookings/{bookingId}", 1L)
                        .param("approved", "true")
                        .header(CUSTOM_REQUEST_HEADER_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(approvedBooking.getId()))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void getBookingByBookerIdOrOwnerId_ShouldReturnBookingDto() throws Exception {
        Mockito.when(bookingService.getBookingByBookerIdOrOwnerId(1L, 1L))
                .thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/{bookingId}", 1L)
                        .header(CUSTOM_REQUEST_HEADER_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    void getUserBookings_ShouldReturnListBookings() throws Exception {
        Mockito.when(bookingService.getUserBookings(1L, StateParam.ALL))
                .thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings")
                        .param("state", "ALL")
                        .header(CUSTOM_REQUEST_HEADER_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0].id").value(bookingDto.getId()))
                .andExpect(jsonPath("[0].status").value("WAITING"));
    }

    @Test
    void getBookingsForItemsByOwnerId_ShouldReturnListBookings() throws Exception {
        Mockito.when(bookingService.getBookingsForItemsByOwnerId(1L, StateParam.ALL))
                .thenReturn(List.of(bookingDto));

        mockMvc.perform(get("/bookings/owner")
                        .param("state", "ALL")
                        .header(CUSTOM_REQUEST_HEADER_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookingDto.getId()))
                .andExpect(jsonPath("$[0].status").value("WAITING"));
    }

}