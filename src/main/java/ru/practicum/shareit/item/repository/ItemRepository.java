package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item add(Item item);

    Item update(Item item, Long id, Long userId);

    Item getById(Long userId, Long id);

    List<Item> getAllUserItems(Long userId);

    List<Item> searchByName(String text);
}
