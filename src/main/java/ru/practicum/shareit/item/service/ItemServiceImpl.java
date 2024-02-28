package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper mapper;

    @Override
    public ItemDto addItem(ItemDto itemDto, Long userId) {
        Item item = mapper.toItem(itemDto);
        setOwner(item, userId);
        log.info("Добавление предмета {}, пользователем с id {}", item, userId);
        return mapper.toDto(itemRepository.add(item));
    }

    @Override
    public ItemDto updateItem(Long userId, Long id, ItemDto itemDto) {
        Item item = mapper.toItem(itemDto);
        log.info("Обновление предмета {}, пользователем с id {}", item, userId);
        return mapper.toDto(itemRepository.update(item, id, userId));
    }

    @Override
    public ItemDto getItemById(Long userId, Long id) {
        log.info("Получение предмета с id {}, пользователя {}", id, userId);
        return mapper.toDto(itemRepository.getById(userId, id));
    }

    @Override
    public List<ItemDto> getAllUserItems(Long userId) {
        log.info("Получение всех предметов пользователя {}", userId);
        return itemRepository.getAllUserItems(userId).stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItemsByName(String text) {
        log.info("Поиск предметов содержащих в названии {}", text);
        return itemRepository.searchByName(text).stream().map(mapper::toDto).collect(Collectors.toList());
    }

    private void setOwner(Item item, Long userId) {
        final User user = userRepository.getById(userId);
        item.setOwner(user);
    }
}
