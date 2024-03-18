package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.CommentText;

import java.util.List;

public interface ItemService {
    ItemDto addItem(ItemDto item, Long userId);

    ItemDto updateItem(Long userId, Long id, ItemDto itemDto);

    ItemDto getItemById(Long userId, Long id);

    List<ItemDto> getAllUserItems(Long userId);

    List<ItemDto> searchItemsByName(String text);

    CommentDto addComment(Long userId, Long itemId, CommentText text);
}

