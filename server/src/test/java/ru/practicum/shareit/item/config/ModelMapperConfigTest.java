package ru.practicum.shareit.item.config;

import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ModelMapperConfigTest {
    @Test
    public void testModelMapperConfig() {
        ModelMapperConfig config = new ModelMapperConfig();
        ModelMapper modelMapper = config.modelMapper();
        assertNotNull(modelMapper);

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Test comment");

        User user = new User();
        user.setName("John");
        comment.setUser(user);

        CommentDto commentDto = modelMapper.map(comment, CommentDto.class);
        assertEquals("Test comment", commentDto.getText());
        assertEquals("John", commentDto.getAuthorName());

        Item item = new Item();
        item.setId(1L);
        item.setName("Test item");
        item.setDescription("Test description");
        item.setAvailable(true);

        ItemRequestDto itemRequestDto = modelMapper.map(item, ItemRequestDto.class);
        assertEquals("Test item", itemRequestDto.getName());
        assertEquals("Test description", itemRequestDto.getDescription());
        assertEquals(true, itemRequestDto.getAvailable());

        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setName("Updated item");
        requestDto.setDescription("Updated description");
        requestDto.setAvailable(false);

        Item mappedItem = modelMapper.map(requestDto, Item.class);
        assertEquals("Updated item", mappedItem.getName());
        assertEquals("Updated description", mappedItem.getDescription());
        assertEquals(false, mappedItem.getAvailable());
    }
}