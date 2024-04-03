package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findItemsByOwnerIdOrderById(Long id);

    List<Item> findItemsByNameContainsIgnoreCaseAndAvailableIsTrueOrDescriptionContainsIgnoreCaseAndAvailableIsTrue(String name, String description);

    List<Item> findItemsByRequestIdIn(List<Long> ids);

    Item findItemByRequestId(Long requestId);

}
