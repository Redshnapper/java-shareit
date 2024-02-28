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
public class InMemoryItemRepository implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    protected static Long idGenerator = 1L;
    private final UserRepository userRepository;


    @Override
    public Item add(Item item) {
        item.setId(idGenerator++);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item item, Long id, Long userId) {
        if (!items.containsKey(id)) {
            throw new NotFoundException("Предмет с id " + id + " не найден");
        }
        if (!checkItemOwner(id, userId)) {
            throw new NotFoundException("У данной вещи другой владелец!");
        }
        item.setOwner(userRepository.getById(userId));
        item.setId(id);
        return checkUpdatesAndUpdateItem(item);
    }

    @Override
    public Item getById(Long userId, Long id) {
        if (!items.containsKey(id)) {
            throw new NotFoundException("Предмет с id " + id + " не найден");
        }
        Item item = items.get(id);
//        if (!item.getOwner().getId().equals(userId)) {
//            throw new ValidationException("У данной вещи другой владелец!");
//        }
        return item;
    }

    @Override
    public List<Item> getAllUserItems(Long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
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

    private Item checkUpdatesAndUpdateItem(Item item) {
        Item updatedItem = items.get(item.getId());
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
}
