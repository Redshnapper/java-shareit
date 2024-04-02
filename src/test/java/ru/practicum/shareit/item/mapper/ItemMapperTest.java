package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.model.Item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ItemMapperTest {

    @Autowired
    private ItemMapper itemMapper;

    @Test
    public void testToDto() {
        Item item = new Item(1L, "Test Item", "Test Description", true, null, null);
        ItemDto itemDto = itemMapper.toDto(item);
        assertNotNull(itemDto);

        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getAvailable(), itemDto.getAvailable());
    }

    @Test
    public void testToItem() {
        ItemDto itemDto = new ItemDto(1L, "Test Item", "Test Description", true, null, null, null);
        Item item = itemMapper.toItem(itemDto);

        assertNotNull(item);
        assertEquals(itemDto.getId(), item.getId());
        assertEquals(itemDto.getName(), item.getName());
        assertEquals(itemDto.getDescription(), item.getDescription());
        assertEquals(itemDto.getAvailable(), item.getAvailable());
    }

    @Test
    public void testToItemFromRequestDto() {
        ItemRequestDto requestDto = new ItemRequestDto(1L, "Test Item", "Test Description", true, 123L);
        Item item = itemMapper.toItem(requestDto);

        assertNotNull(item);
        assertEquals(requestDto.getId(), item.getId());
        assertEquals(requestDto.getName(), item.getName());
        assertEquals(requestDto.getDescription(), item.getDescription());
        assertEquals(requestDto.getAvailable(), item.getAvailable());
    }
}