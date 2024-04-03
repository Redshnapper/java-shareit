package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.BookingView;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.CommentText;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.InMemoryItemRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserView;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private RequestRepository requestRepository;
    private ItemService itemService;
    private ItemMapper mapper;
    private final ModelMapper modelMapper = new ModelMapper();
    private final InMemoryItemRepository memoryItemRepository = new InMemoryItemRepository(userRepository);

    @BeforeEach
    public void setUp() {
        mapper = new ItemMapper() {
            public ItemDto toDto(Item item) {
                ItemDto itemDto = new ItemDto();
                itemDto.setId(item.getId());
                itemDto.setName(item.getName());
                itemDto.setDescription(item.getDescription());
                itemDto.setAvailable(item.getAvailable());
                return itemDto;
            }

            public Item toItem(ItemDto itemDto) {
                Item item = new Item();
                item.setId(itemDto.getId());
                item.setName(itemDto.getName());
                item.setDescription(itemDto.getDescription());
                item.setAvailable(itemDto.getAvailable());
                return item;
            }

            public Item toItem(ItemRequestDto itemDto) {
                Item item = new Item();
                item.setId(itemDto.getId());
                item.setName(itemDto.getName());
                item.setDescription(itemDto.getDescription());
                item.setAvailable(itemDto.getAvailable());
                return item;
            }
        };

        itemService = new ItemServiceImpl(
                itemRepository,
                userRepository,
                memoryItemRepository,
                bookingRepository,
                mapper,
                commentRepository,
                modelMapper,
                requestRepository);
    }

    @Test
    void addItemTestCorrectWithoutRequest() {
        Long userId = 1L;
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        User user = new User(userId);
        Item item = mapper.toItem(itemRequestDto);
        item.setOwner(user);

        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));
        when(userRepository.getReferenceById(userId)).thenReturn(user);
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemRequestDto added = itemService.addItem(itemRequestDto, userId);
        assertNotNull(added);
    }

    @Test
    void addItemTestCorrectWithRequest() {
        Long userId = 1L;
        Long requestId = 2L;
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setRequestId(requestId);
        itemRequestDto.setName("name");
        itemRequestDto.setDescription("description");
        itemRequestDto.setAvailable(true);

        Request request = new Request();
        request.setId(requestId);
        User user = new User(userId);
        Item item = mapper.toItem(itemRequestDto);
        item.setOwner(user);
        item.setRequest(request);

        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));
        when(userRepository.getReferenceById(userId)).thenReturn(user);
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        when(requestRepository.findById(requestId)).thenReturn(Optional.of(request));

        ItemRequestDto added = itemService.addItem(itemRequestDto, userId);
        assertNotNull(added);
        assertEquals(itemRequestDto.getId(), added.getId());
        assertEquals(itemRequestDto.getName(), added.getName());
        assertEquals(itemRequestDto.getDescription(), added.getDescription());
        assertEquals(itemRequestDto.getAvailable(), added.getAvailable());
        assertEquals(requestId, added.getRequestId());
    }

    @Test
    void updateItemTrowsNotFoundException() {
        Long userId = 1L;
        Long itemId = 2L;
        ItemDto itemDto = new ItemDto();
        itemDto.setName("name");
        itemDto.setDescription("description");
        itemDto.setAvailable(true);
        Item item = mapper.toItem(itemDto);
        User user = new User(userId);
        item.setOwner(user);

        when(itemRepository.getReferenceById(itemId)).thenReturn(item);
        assertThrows(NotFoundException.class, () -> itemService.updateItem(3L, itemId, itemDto));
    }

    @Test
    void updateItemCorrect() {
        Long userId = 1L;
        Long itemId = 2L;
        ItemDto itemDto = new ItemDto();
        itemDto.setName("name");
        itemDto.setDescription("description");
        itemDto.setAvailable(true);
        Item item = mapper.toItem(itemDto);
        User user = new User(userId);
        item.setOwner(user);
        Item savedItem = new Item("updated name", "updated description");
        savedItem.setAvailable(false);
        savedItem.setId(itemId);

        when(itemRepository.getReferenceById(itemId)).thenReturn(item);
        when(userRepository.getReferenceById(userId)).thenReturn(user);
        when(itemRepository.save(any(Item.class))).thenReturn(savedItem);

        ItemDto updated = itemService.updateItem(1L, itemId, itemDto);

        assertNotNull(updated);
        assertEquals(itemId, updated.getId());
        assertEquals(false, updated.getAvailable());
        assertEquals("updated name", updated.getName());
        assertEquals("updated description", updated.getDescription());
    }

    @Test
    void getItemByIdThrowsNotFoundException() {
        assertThrows(NotFoundException.class, () -> itemService.getItemById(1L, 1L));
    }

    @Test
    void getItemByIdNoCommentsAndBookings() {
        Long itemId = 1L;
        Long ownerId = 1L;
        User owner = new User();
        owner.setId(ownerId);
        Item item = new Item();
        item.setId(itemId);
        item.setOwner(owner);

        when(itemRepository.getReferenceById(itemId)).thenReturn(item);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(commentRepository.findCommentByItemIdOrderById(itemId)).thenReturn(new ArrayList<>());

        ItemDto itemById = itemService.getItemById(2L, itemId);

        assertNotNull(itemById);
        assertNotNull(itemById.getComments());
        assertNull(itemById.getLastBooking());
        assertNull(itemById.getNextBooking());
    }

    @Test
    void getItemByIdWithCommentsAndLastBookingBetween() {
        Long itemId = 1L;
        Long ownerId = 1L;
        User owner = new User();
        owner.setId(ownerId);
        Item item = new Item();
        item.setId(itemId);
        item.setOwner(owner);
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setItem(item);
        Comment comment2 = new Comment();
        comment2.setId(2L);
        comment2.setItem(item);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime before = now.minusHours(1);
        LocalDateTime after = now.plusHours(1);
        User booker = new User();
        booker.setId(2L);
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setEnd(before);
        booking.setStart(after);
        booking.setStatus(BookingStatus.APPROVED);
        BookingView view = new BookingView() {
            @Override
            public Long getId() {
                return 1L;
            }

            @Override
            public UserView getBooker() {
                return () -> 2L;
            }
        };

        when(itemRepository.getReferenceById(itemId)).thenReturn(item);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(commentRepository.findCommentByItemIdOrderById(itemId)).thenReturn(List.of(comment, comment2));
        when(bookingRepository.findByItemIdAndEndIsBeforeAndStatusNotInOrderByEndDesc(eq(itemId),
                any(LocalDateTime.class), ArgumentMatchers.anyList())).thenReturn(new ArrayList<>());
        when(bookingRepository.findByItemIdAndStartIsAfterAndStatusNotInOrderByStart(eq(itemId),
                any(LocalDateTime.class), ArgumentMatchers.anyList())).thenReturn(List.of(view));
        when(bookingRepository.findByItemIdAndStartIsBeforeAndEndIsAfterAndStatusNotInOrderByEndDesc(eq(itemId),
                any(LocalDateTime.class), any(LocalDateTime.class), ArgumentMatchers.anyList())).thenReturn(List.of(view));

        ItemDto itemById = itemService.getItemById(1L, itemId);

        assertNotNull(itemById);
        assertNotNull(itemById.getComments());
        assertNotNull(itemById.getLastBooking());
        assertNotNull(itemById.getNextBooking());
        assertEquals(2L, itemById.getComments().get(1).getId());
        assertEquals(1L, itemById.getComments().get(0).getId());
        assertEquals(1L, itemById.getLastBooking().getId());
        assertEquals(2L, itemById.getLastBooking().getBookerId());
        assertEquals(1L, itemById.getNextBooking().getId());
        assertEquals(2L, itemById.getNextBooking().getBookerId());
    }

    @Test
    void getItemByIdWithCommentsAndLastBooking() {
        Long itemId = 1L;
        Long ownerId = 1L;
        User owner = new User();
        owner.setId(ownerId);
        Item item = new Item();
        item.setId(itemId);
        item.setOwner(owner);
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setItem(item);
        Comment comment2 = new Comment();
        comment2.setId(2L);
        comment2.setItem(item);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime before = now.minusHours(1);
        LocalDateTime after = now.plusHours(1);
        User booker = new User();
        booker.setId(2L);
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setEnd(before);
        booking.setStart(after);
        booking.setStatus(BookingStatus.APPROVED);
        BookingView view = new BookingView() {
            @Override
            public Long getId() {
                return 1L;
            }

            @Override
            public UserView getBooker() {
                return () -> 2L;
            }
        };

        when(itemRepository.getReferenceById(itemId)).thenReturn(item);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(commentRepository.findCommentByItemIdOrderById(itemId)).thenReturn(List.of(comment, comment2));
        when(bookingRepository.findByItemIdAndEndIsBeforeAndStatusNotInOrderByEndDesc(eq(itemId),
                any(LocalDateTime.class), ArgumentMatchers.anyList())).thenReturn(List.of(view));
        when(bookingRepository.findByItemIdAndStartIsAfterAndStatusNotInOrderByStart(eq(itemId),
                any(LocalDateTime.class), ArgumentMatchers.anyList())).thenReturn(List.of(view));
        when(bookingRepository.findByItemIdAndStartIsBeforeAndEndIsAfterAndStatusNotInOrderByEndDesc(eq(itemId),
                any(LocalDateTime.class), any(LocalDateTime.class), ArgumentMatchers.anyList())).thenReturn(new ArrayList<>());

        ItemDto itemById = itemService.getItemById(1L, itemId);

        assertNotNull(itemById);
        assertNotNull(itemById.getComments());
        assertNotNull(itemById.getLastBooking());
        assertNotNull(itemById.getNextBooking());
        assertEquals(2L, itemById.getComments().get(1).getId());
        assertEquals(1L, itemById.getComments().get(0).getId());
        assertEquals(1L, itemById.getLastBooking().getId());
        assertEquals(2L, itemById.getLastBooking().getBookerId());
        assertEquals(1L, itemById.getNextBooking().getId());
        assertEquals(2L, itemById.getNextBooking().getBookerId());
    }

    @Test
    void getItemByIdWithCommentsAndEmptyBookings() {
        Long itemId = 1L;
        Long ownerId = 1L;
        User owner = new User();
        owner.setId(ownerId);
        Item item = new Item();
        item.setId(itemId);
        item.setOwner(owner);
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setItem(item);
        Comment comment2 = new Comment();
        comment2.setId(2L);
        comment2.setItem(item);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime before = now.minusHours(1);
        LocalDateTime after = now.plusHours(1);
        User booker = new User();
        booker.setId(2L);
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setEnd(before);
        booking.setStart(after);
        booking.setStatus(BookingStatus.APPROVED);

        when(itemRepository.getReferenceById(itemId)).thenReturn(item);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(commentRepository.findCommentByItemIdOrderById(itemId)).thenReturn(List.of(comment, comment2));

        when(bookingRepository.findByItemIdAndEndIsBeforeAndStatusNotInOrderByEndDesc(eq(itemId),
                any(LocalDateTime.class), ArgumentMatchers.anyList())).thenReturn(new ArrayList<>());
        when(bookingRepository.findByItemIdAndStartIsAfterAndStatusNotInOrderByStart(eq(itemId),
                any(LocalDateTime.class), ArgumentMatchers.anyList())).thenReturn(new ArrayList<>());
        when(bookingRepository.findByItemIdAndStartIsBeforeAndEndIsAfterAndStatusNotInOrderByEndDesc(eq(itemId),
                any(LocalDateTime.class), any(LocalDateTime.class), ArgumentMatchers.anyList())).thenReturn(new ArrayList<>());

        ItemDto itemById = itemService.getItemById(1L, itemId);

        assertNotNull(itemById);
        assertNotNull(itemById.getComments());
        assertNull(itemById.getLastBooking());
        assertNull(itemById.getNextBooking());
        assertEquals(2L, itemById.getComments().get(1).getId());
        assertEquals(1L, itemById.getComments().get(0).getId());
    }


    @Test
    void getAllUserItemsEmptyBookings() {
        Long itemId = 1L;
        Long ownerId = 1L;
        User owner = new User();
        owner.setId(ownerId);
        Item item = new Item();
        item.setId(itemId);
        item.setOwner(owner);
        Item item2 = new Item();
        item2.setId(itemId + 1L);
        item2.setOwner(owner);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime before = now.minusHours(1);
        LocalDateTime after = now.plusHours(1);
        User booker = new User();
        booker.setId(2L);
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setEnd(before);
        booking.setStart(after);
        booking.setStatus(BookingStatus.APPROVED);

        when(itemRepository.findItemsByOwnerIdOrderById(ownerId)).thenReturn(List.of(item, item2));
        when(itemRepository.getReferenceById(itemId)).thenReturn(item);
        when(itemRepository.getReferenceById(itemId + 1L)).thenReturn(item2);
        when(bookingRepository.findByItemIdAndEndIsBeforeAndStatusNotInOrderByEndDesc(eq(itemId),
                any(LocalDateTime.class), ArgumentMatchers.anyList())).thenReturn(new ArrayList<>());
        when(bookingRepository.findByItemIdAndStartIsAfterAndStatusNotInOrderByStart(eq(itemId),
                any(LocalDateTime.class), ArgumentMatchers.anyList())).thenReturn(new ArrayList<>());
        when(bookingRepository.findByItemIdAndStartIsBeforeAndEndIsAfterAndStatusNotInOrderByEndDesc(eq(itemId),
                any(LocalDateTime.class), any(LocalDateTime.class), ArgumentMatchers.anyList())).thenReturn(new ArrayList<>());

        List<ItemDto> allUserItems = itemService.getAllUserItems(ownerId);

        assertNotNull(allUserItems);
        assertEquals(1L, allUserItems.get(0).getId());
        assertEquals(2L, allUserItems.get(1).getId());
        assertNull(allUserItems.get(0).getNextBooking());
        assertNull(allUserItems.get(0).getLastBooking());
        assertNull(allUserItems.get(1).getNextBooking());
        assertNull(allUserItems.get(1).getLastBooking());
    }

    @Test
    void searchItemsByNameEmpty() {
        assertEquals(new ArrayList<>(), itemService.searchItemsByName(""));
    }

    @Test
    void searchItemsByNameAll() {
        Item item = new Item("name", "descripT");
        Item item2 = new Item("text", "some TEXT");


        when(itemRepository.findItemsByNameContainsIgnoreCaseAndAvailableIsTrueOrDescriptionContainsIgnoreCaseAndAvailableIsTrue(any(String.class), any(String.class)))
                .thenReturn(List.of(item, item2));
        assertEquals(List.of(mapper.toDto(item), mapper.toDto(item2)), itemService.searchItemsByName(" "));
    }

    @Test
    void addCommentThrowsBadRequestException() {
        when(bookingRepository.findBookingByItemIdAndBookerIdAndStatusNotInAndEndBefore(anyLong(),
                anyLong(), ArgumentMatchers.anyList(), any(LocalDateTime.class))).thenReturn(new ArrayList<>());

        assertThrows(BadRequestException.class, () -> itemService.addComment(1L,
                1L, new CommentText("text")));
    }

    @Test
    void addCommentCorrect() {
        Long itemId = 1L;
        Long ownerId = 1L;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime before = now.minusHours(1);
        LocalDateTime after = now.plusHours(1);
        CommentText text = new CommentText("text");
        User owner = User.builder()
                .id(ownerId)
                .build();
        User booker = User.builder()
                .id(2L)
                .name("commentator")
                .build();
        Item item = Item.builder()
                .id(itemId)
                .owner(owner)
                .build();
        Item item2 = Item.builder()
                .id(2L)
                .owner(owner)
                .build();
        Booking booking = Booking.builder()
                .id(1L)
                .booker(booker)
                .item(item)
                .build();
        Booking booking2 = Booking.builder()
                .id(2L)
                .booker(booker)
                .item(item2)
                .build();

        when(bookingRepository.findBookingByItemIdAndBookerIdAndStatusNotInAndEndBefore(anyLong(),
                anyLong(), ArgumentMatchers.anyList(), any(LocalDateTime.class))).thenReturn(List.of(booking, booking2));
        when(commentRepository.save(any(Comment.class))).thenReturn(new Comment());

        CommentDto comment = itemService.addComment(2L, 1L, text);
        assertNotNull(comment);
    }
}