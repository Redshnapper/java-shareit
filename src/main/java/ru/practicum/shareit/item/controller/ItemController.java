package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.CommentText;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.util.Constants;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto addItem(@RequestHeader(Constants.USER_HEADER_ID) Long userId,
                           @RequestBody @Valid ItemDto itemDto) {
        log.info("Добавление предмета item = {}, пользователем userId = {}", itemDto, userId);
        return itemService.addItem(itemDto, userId);
    }

    @PatchMapping("{id}")
    public ItemDto updateItem(@RequestHeader(Constants.USER_HEADER_ID) Long userId,
                              @PathVariable Long id,
                              @RequestBody ItemDto itemDto) {
        log.info("Обновление предмета, id = {}, item = {}, пользователем userId = {}", id, itemDto, userId);
        return itemService.updateItem(userId, id, itemDto);
    }

    @GetMapping("{id}")
    public ItemDto getItemById(@RequestHeader(Constants.USER_HEADER_ID) Long userId,
                               @PathVariable Long id) {
        log.info("Получение предмета с id = {}, пользователем userId = {}", id, userId);
        return itemService.getItemById(userId, id);
    }

    @GetMapping
    public List<ItemDto> getAllUserItems(@RequestHeader(Constants.USER_HEADER_ID) Long userId) {
        log.info("Получение всех предметов пользователем userId = {}", userId);
        return itemService.getAllUserItems(userId);
    }

    @GetMapping("search")
    public List<ItemDto> searchItemsByName(@RequestParam(required = false, name = "text") String text) {
        log.info("Поиск предметов содержащих в названии {}", text);
        return itemService.searchItemsByName(text);
    }

    @PostMapping("{itemId}/comment")
    public CommentDto addComment(@Valid @RequestHeader(Constants.USER_HEADER_ID) Long userId,
                                 @PathVariable Long itemId, @RequestBody @Valid CommentText text) {
        log.info("Добавление комментария пользователем userId = {}, itemId = {}, text {}", userId, itemId, text);
        return itemService.addComment(userId, itemId, text);
    }

}
