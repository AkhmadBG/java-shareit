package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.NewCommentAddRequest;
import ru.practicum.shareit.item.dto.NewItemAddRequest;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

@Service
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> addItem(Long userId, NewItemAddRequest newItemAddRequest) {
        return post("", userId, newItemAddRequest);
    }

    public ResponseEntity<Object> updateItem(Long itemId, Long userId, UpdateItemRequest updateItemRequest) {
        return patch("/" + itemId, userId, updateItemRequest);
    }

    public ResponseEntity<Object> getItemDtoWithDateById(Long itemId, Long userId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getItemsByUserId(Long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> deleteItem(Long itemId, Long userId) {
        return delete("/" + itemId, userId);
    }

    public ResponseEntity<Object> searchItemsByText(Long userId, String text) {
        return get("/search?text=" + text, userId);
    }

    public ResponseEntity<Object> addComment(Long userId, Long itemId, NewCommentAddRequest newCommentAddRequest) {
        return post("/" + itemId + "/comment", userId, newCommentAddRequest);
    }

}