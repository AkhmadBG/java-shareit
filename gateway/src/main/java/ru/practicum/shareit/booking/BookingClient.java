package ru.practicum.shareit.booking;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

import ru.practicum.shareit.booking.dto.NewBookingAddRequest;
import ru.practicum.shareit.booking.dto.StateParam;
import ru.practicum.shareit.client.BaseClient;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> addBooking(Long userId, NewBookingAddRequest newBookingAddRequest) {
        return post("", userId, newBookingAddRequest);
    }

    public ResponseEntity<Object> approvedBooking(Long userId, Long bookingId, Boolean approved) {
        return patch1("/" + bookingId + "?approved=" + approved, (Long) userId);
    }

    public ResponseEntity<Object> getBookingByBookerIdOrOwnerId(Long userId, Long bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> getUserBookings(Long userId, StateParam stateParam) {
        Map<String, Object> parameters = Map.of("state", stateParam);
        return get("", userId, parameters);
    }

    public ResponseEntity<Object> getBookingsForItemsByOwnerId(Long userId, StateParam stateParam) {
        Map<String, Object> parameters = Map.of("state", stateParam);
        return get("/owner", userId, parameters);
    }

}