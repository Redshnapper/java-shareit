package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class InMemoryItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    protected Long idGenerator = 1L;
    private final UserRepository userRepository;


    public Item add(Item item) {
        item.setId(idGenerator++);
        items.put(item.getId(), item);
        return item;
    }

    public Item update(Item item, Long id, Long userId) {
        if (!items.containsKey(id)) {
            throw new NotFoundException("Предмет с id " + id + " не найден");
        }
        if (!checkItemOwner(id, userId)) {
            throw new NotFoundException("У данной вещи другой владелец!");
        }
        item.setOwner(userRepository.getReferenceById(userId));
        item.setId(id);
        Item savedItem = items.get(id);
        Item updateItem = checkUpdatesAndUpdateItem(item, savedItem);
        return items.put(id, updateItem);
    }


    public Item getById(Long userId, Long id) {
        if (!items.containsKey(id)) {
            throw new NotFoundException("Предмет с id " + id + " не найден");
        }
        return items.get(id);
    }

    public List<Item> getAllUserItems(Long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .collect(Collectors.toList());
    }

    public List<Item> searchByName(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        return items.values().stream()
                .filter(item -> (item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                        && item.getAvailable().equals(true))
                .collect(Collectors.toList());
    }

    private boolean checkItemOwner(Long id, Long userId) {
        return items.get(id).getOwner().getId().equals(userId);

    }

    public Item checkUpdatesAndUpdateItem(Item item, Item updatedItem) {
        String name = item.getName();
        String description = item.getDescription();
        Boolean available = item.getAvailable();

        if (name != null) {
            updatedItem.setName(name);
        }
        if (description != null) {
            updatedItem.setDescription(description);
        }
        if (available != null) {
            updatedItem.setAvailable(available);
        }
        items.put(item.getId(), updatedItem);
        return updatedItem;

    }

    public void removeAll() {
        idGenerator = 1L;
        items.clear();
    }
}
