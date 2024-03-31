package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingItemDto;
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
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final InMemoryItemRepository memoryItemRepository;
    private final BookingRepository bookingRepository;
    private final ItemMapper itemMapper;
    private final CommentRepository commentRepository;
    private final ModelMapper modelMapper;
    private final RequestRepository requestRepository;
    private final List<BookingStatus> wrongStatuses = List.of(BookingStatus.WAITING, BookingStatus.REJECTED, BookingStatus.CANCELED);

    @Override
    public ItemRequestDto addItem(ItemRequestDto itemDto, Long userId) {
        Item item = itemMapper.toItem(itemDto);
        checkUserExists(userId);
        setOwner(item, userId);
        Long requestId = itemDto.getRequestId();
        if (requestId != null) {
            Request request = requestRepository.findById(requestId).orElseThrow();
            item.setRequest(request);
        }
        Item save = itemRepository.save(item);
        return modelMapper.map(save, ItemRequestDto.class);
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        Item item = itemMapper.toItem(itemDto);
        if (!checkItemOwner(itemId, userId)) {
            throw new NotFoundException();
        }
        User owner = userRepository.getReferenceById(userId);
        item.setOwner(owner);
        item.setId(itemId);
        Item savedItem = itemRepository.getReferenceById(itemId);
        Item updateItem = memoryItemRepository.checkUpdatesAndUpdateItem(item, savedItem);
        return itemMapper.toDto(itemRepository.save(updateItem));
    }


    @Override
    public ItemDto getItemById(Long userId, Long itemId) {
        checkItemExists(itemId);
        ItemDto itemDto = itemMapper.toDto(itemRepository.getReferenceById(itemId));
        itemDto.setComments(getCommentsForItem(itemId));
        return setItemBookings(itemDto, userId, itemId);
    }

    @Override
    public List<ItemDto> getAllUserItems(Long userId) {
        List<ItemDto> collect = itemRepository.findItemsByOwnerIdOrderById(userId)
                .stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
        List<ItemDto> withBookings = new ArrayList<>();
        for (ItemDto itemDto : collect) {
            Long id = itemDto.getId();
            withBookings.add(setItemBookings(itemDto, userId, id));
        }
        return withBookings;
    }

    @Override
    public List<ItemDto> searchItemsByName(String text) {
        if (!text.isEmpty()) {
            return itemRepository.findItemsByNameContainsIgnoreCaseAndAvailableIsTrueOrDescriptionContainsIgnoreCaseAndAvailableIsTrue(text, text)
                    .stream()
                    .map(itemMapper::toDto)
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentText text) {
        List<Booking> bookings = bookingRepository.findBookingByItemIdAndBookerIdAndStatusNotInAndEndBefore(itemId,
                userId, wrongStatuses, LocalDateTime.now());
        if (!bookings.isEmpty()) {
            Item item = bookings.get(0).getItem();
            User user = bookings.get(0).getBooker();
            Comment comment = new Comment();
            comment.setText(text.getText());
            comment.setItem(item);
            comment.setUser(user);
            comment.setCreated(LocalDateTime.now());
            CommentDto map = modelMapper.map(comment, CommentDto.class);
            ItemDto dto = itemMapper.toDto(item);
            dto.getComments().add(map);
            commentRepository.save(comment);
            return modelMapper.map(comment, CommentDto.class);
        }
        throw new BadRequestException();
    }

    private List<CommentDto> getCommentsForItem(Long itemId) {
        List<Comment> allById = commentRepository.findCommentByItemIdOrderById(itemId);
        return allById.stream().map(comment -> modelMapper.map(comment, CommentDto.class)).collect(Collectors.toList());
    }

    private BookingItemDto toBookingItemDto(BookingView view) {
        BookingItemDto bookingItemDto = new BookingItemDto();
        bookingItemDto.setId(view.getId());
        bookingItemDto.setBookerId(view.getBooker().getId());
        return bookingItemDto;
    }

    private void checkItemExists(Long itemId) {
        itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Предмет с id " + itemId + " не найден"));
    }

    private void setOwner(Item item, Long userId) {
        User user = userRepository.getReferenceById(userId);
        item.setOwner(user);
    }

    private boolean checkItemOwner(Long itemId, Long userId) {
        return itemRepository.getReferenceById(itemId).getOwner().getId().equals(userId);
    }

    private void checkUserExists(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
    }

    private ItemDto setItemBookings(ItemDto itemDto, Long userId, Long itemId) {
        if (!checkItemOwner(itemId, userId)) {
            return itemDto;
        }
        List<BookingView> last = bookingRepository.findByItemIdAndEndIsBeforeAndStatusNotInOrderByEndDesc(itemId,
                LocalDateTime.now(), wrongStatuses);
        List<BookingView> between = bookingRepository.findByItemIdAndStartIsBeforeAndEndIsAfterAndStatusNotInOrderByEndDesc(itemId,
                LocalDateTime.now(), LocalDateTime.now(), wrongStatuses);
        List<BookingView> next = bookingRepository.findByItemIdAndStartIsAfterAndStatusNotInOrderByStart(itemId,
                LocalDateTime.now(), wrongStatuses);
        if (!last.isEmpty()) {
            itemDto.setLastBooking(toBookingItemDto(last.get(0)));
        }
        if (!next.isEmpty()) {
            itemDto.setNextBooking(toBookingItemDto(next.get(0)));
        }
        if (!between.isEmpty()) {
            itemDto.setLastBooking(toBookingItemDto(between.get(0)));
        }
        return itemDto;
    }
}
