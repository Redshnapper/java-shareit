package ru.practicum.shareit.Item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Item.dto.CommentText;
import ru.practicum.shareit.Item.dto.ItemDto;
import ru.practicum.shareit.Item.dto.ItemRequestDto;

import javax.validation.Valid;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @RequestBody @Valid ItemRequestDto itemDto) {
        log.info("Adding item: {}, user: {}", itemDto, userId);
        return itemClient.addItem(userId, itemDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long id,
                                             @RequestBody ItemDto itemDto) {
        log.info("Updating item, id = {}, item = {}, user: {}", id, itemDto, userId);
        return itemClient.updateItem(userId, id, itemDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItemById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @PathVariable Long id) {
        log.info("Getting item by id = {}, user: {}", id, userId);
        return itemClient.getItemById(userId, id);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUserItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Getting all user items, user: {}", userId);
        return itemClient.getAllUserItems(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItemsByName(@RequestParam(required = false, name = "text") String text) {
        log.info("Searching items by name: {}", text);
        return itemClient.searchItemsByName(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long itemId,
                                             @RequestBody @Valid CommentText text) {
        log.info("Adding comment: userId = {}, itemId = {}, text = {}", userId, itemId, text);
        return itemClient.addComment(userId, itemId, text);
    }
}

