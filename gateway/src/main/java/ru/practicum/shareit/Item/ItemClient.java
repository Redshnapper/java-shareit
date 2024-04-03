package ru.practicum.shareit.Item;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.Item.dto.CommentText;
import ru.practicum.shareit.Item.dto.ItemDto;
import ru.practicum.shareit.Item.dto.ItemRequestDto;
import ru.practicum.shareit.client.BaseClient;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build());
    }

    public ResponseEntity<Object> addItem(Long userId, ItemRequestDto itemDto) {
        return post("", userId, itemDto);
    }

    public ResponseEntity<Object> updateItem(Long userId, Long id, ItemDto itemDto) {
        return patch("/{id}", userId, Map.of("id", id), itemDto);
    }

    public ResponseEntity<Object> getItemById(Long userId, Long id) {
        return get("/{id}", userId, Map.of("id", id));
    }

    public ResponseEntity<Object> getAllUserItems(Long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> searchItemsByName(String text) {
        return get("/search?text={text}", null, Map.of("text", text));
    }

    public ResponseEntity<Object> addComment(Long userId, Long itemId, CommentText text) {
        return post("/{itemId}/comment", userId, Map.of("itemId", itemId), text);
    }
}
