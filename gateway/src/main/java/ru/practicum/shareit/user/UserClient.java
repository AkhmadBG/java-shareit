package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.user.dto.NewUserAddRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.util.PageResponse;

import java.util.Map;

@Service
public class UserClient extends BaseClient {

    private static final String API_PREFIX = "/users";

    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> addUser(NewUserAddRequest newUserAddRequest) {
        return post("", newUserAddRequest);
    }

    public ResponseEntity<Object> updateUserDto(Long userId, UpdateUserRequest updateUserRequest) {
        return patch("/" + userId, userId, updateUserRequest);
    }

    public ResponseEntity<Object> getUserDtoById(Long userId) {
        return get("/" + userId, userId);
    }

    public ResponseEntity<PageResponse<Object>> getAllUsers(int page, int size) {
        Map<String, Object> params = Map.of("page", page, "size", size);
        return getPage("", params, new ParameterizedTypeReference<>() {
        });
    }

    public ResponseEntity<Object> deleteUser(Long userId) {
        return delete("/" + userId, userId);
    }

}