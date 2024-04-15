package ru.practicum.shareit.item.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.model.CommentText;
import ru.practicum.shareit.item.service.ItemService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @Test
    public void testAddItem() throws Exception {
        ItemRequestDto itemDto = new ItemRequestDto();
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);
        itemDto.setRequestId(1L);
        Long userId = 123L;
        ItemRequestDto savedItemDto = new ItemRequestDto();
        savedItemDto.setId(1L);
        savedItemDto.setName(itemDto.getName());
        savedItemDto.setDescription(itemDto.getDescription());
        savedItemDto.setAvailable(itemDto.getAvailable());
        savedItemDto.setRequestId(itemDto.getRequestId());

        when(itemService.addItem(any(ItemRequestDto.class), any(Long.class))).thenReturn(savedItemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .content("{\"name\":\"Test Item\",\"description\":\"Test Description\",\"available\":true,\"requestId\":1}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Item"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.requestId").value(1L));
    }





    @Test
    public void testAddItem_nullRequestId_returnsOk() throws Exception {
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 123L)
                        .content("{\"name\":\"Test Item\",\"description\":\"Test Description\",\"available\":true}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateItem() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Updated Item");
        itemDto.setDescription("Updated Description");
        itemDto.setAvailable(false);
        itemDto.setLastBooking(new BookingItemDto());
        itemDto.setNextBooking(new BookingItemDto());
        itemDto.setComments(Arrays.asList(new CommentDto(), new CommentDto()));

        Long itemId = 1L;
        Long userId = 123L;

        ItemDto updatedItemDto = new ItemDto();
        updatedItemDto.setId(itemId);
        updatedItemDto.setName(itemDto.getName());
        updatedItemDto.setDescription(itemDto.getDescription());
        updatedItemDto.setAvailable(itemDto.getAvailable());
        updatedItemDto.setLastBooking(itemDto.getLastBooking());
        updatedItemDto.setNextBooking(itemDto.getNextBooking());
        updatedItemDto.setComments(itemDto.getComments());

        when(itemService.updateItem(any(Long.class), any(Long.class), any(ItemDto.class))).thenReturn(updatedItemDto);

        mockMvc.perform(patch("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .content("{\"name\":\"Updated Item\",\"description\":\"Updated Description\",\"available\":false," +
                                "\"lastBooking\":{},\"nextBooking\":{},\"comments\":[{},{}]}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value("Updated Item"))
                .andExpect(jsonPath("$.description").value("Updated Description"))
                .andExpect(jsonPath("$.available").value(false))
                .andExpect(jsonPath("$.lastBooking").isMap())
                .andExpect(jsonPath("$.nextBooking").isMap())
                .andExpect(jsonPath("$.comments").isArray())
                .andExpect(jsonPath("$.comments", hasSize(2)));
    }

    @Test
    public void testGetItemById() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);
        itemDto.setLastBooking(new BookingItemDto());
        itemDto.setNextBooking(new BookingItemDto());
        itemDto.setComments(Arrays.asList(new CommentDto(), new CommentDto()));

        Long itemId = 1L;
        Long userId = 123L;

        when(itemService.getItemById(any(Long.class), any(Long.class))).thenReturn(itemDto);

        mockMvc.perform(get("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value("Test Item"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.lastBooking").isMap())
                .andExpect(jsonPath("$.nextBooking").isMap())
                .andExpect(jsonPath("$.comments").isArray())
                .andExpect(jsonPath("$.comments", hasSize(2)));
    }

    @Test
    public void testGetAllUserItems() throws Exception {
        List<ItemDto> itemDtoList = new ArrayList<>();
        ItemDto item1 = new ItemDto();
        item1.setId(1L);
        item1.setName("Test Item 1");
        item1.setDescription("Test Description 1");
        item1.setAvailable(true);
        item1.setLastBooking(new BookingItemDto());
        item1.setNextBooking(new BookingItemDto());
        item1.setComments(Arrays.asList(new CommentDto(), new CommentDto()));
        ItemDto item2 = new ItemDto();
        item2.setId(2L);
        item2.setName("Test Item 2");
        item2.setDescription("Test Description 2");
        item2.setAvailable(false);
        item2.setLastBooking(new BookingItemDto());
        item2.setNextBooking(new BookingItemDto());
        item2.setComments(Arrays.asList(new CommentDto(), new CommentDto()));
        itemDtoList.add(item1);
        itemDtoList.add(item2);
        Long userId = 123L;
        when(itemService.getAllUserItems(any(Long.class))).thenReturn(itemDtoList);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Test Item 1"))
                .andExpect(jsonPath("$[0].description").value("Test Description 1"))
                .andExpect(jsonPath("$[0].available").value(true))
                .andExpect(jsonPath("$[0].lastBooking").isMap())
                .andExpect(jsonPath("$[0].nextBooking").isMap())
                .andExpect(jsonPath("$[0].comments").isArray())
                .andExpect(jsonPath("$[0].comments", hasSize(2)))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Test Item 2"))
                .andExpect(jsonPath("$[1].description").value("Test Description 2"))
                .andExpect(jsonPath("$[1].available").value(false))
                .andExpect(jsonPath("$[1].lastBooking").isMap())
                .andExpect(jsonPath("$[1].nextBooking").isMap())
                .andExpect(jsonPath("$[1].comments").isArray())
                .andExpect(jsonPath("$[1].comments", hasSize(2)));
    }


    @Test
    public void testSearchItemsByName() throws Exception {
        List<ItemDto> itemDtoList = new ArrayList<>();
        ItemDto item1 = new ItemDto();
        item1.setId(1L);
        item1.setName("Test Item 1");
        item1.setDescription("Test Description 1");
        item1.setAvailable(true);
        ItemDto item2 = new ItemDto();
        item2.setId(2L);
        item2.setName("Test Item 2");
        item2.setDescription("Test Description 2");
        item2.setAvailable(false);
        itemDtoList.add(item1);
        itemDtoList.add(item2);
        String searchText = "Test";

        when(itemService.searchItemsByName(any(String.class))).thenReturn(itemDtoList);
        mockMvc.perform(get("/items/search")
                        .param("text", searchText))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Test Item 1"))
                .andExpect(jsonPath("$[0].description").value("Test Description 1"))
                .andExpect(jsonPath("$[0].available").value(true))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Test Item 2"))
                .andExpect(jsonPath("$[1].description").value("Test Description 2"))
                .andExpect(jsonPath("$[1].available").value(false));
    }

    @Test
    public void testAddComment() throws Exception {
        Long userId = 123L;
        Long itemId = 1L;
        CommentText commentText = new CommentText();
        commentText.setText("Test Comment");
        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText(commentText.getText());

        when(itemService.addComment(any(Long.class), any(Long.class), any(CommentText.class))).thenReturn(commentDto);
        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .content("{\"text\":\"Test Comment\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.text").value("Test Comment"));
    }

}