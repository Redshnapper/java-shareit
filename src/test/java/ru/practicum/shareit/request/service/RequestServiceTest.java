package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.RequestCreateDto;
import ru.practicum.shareit.request.dto.RequestItemsDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestServiceTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RequestRepository requestRepository;
    private final RequestMapper mapper = new RequestMapper();
    private final ModelMapper modelMapper = new ModelMapper();
    private RequestService service;

    @BeforeEach
    void setUp() {
        service = new RequestServiceImpl(
                requestRepository,
                userRepository,
                itemRepository,
                mapper,
                modelMapper
        );
    }

    @Test
    void createRequest() {
        User requestor = User.builder()
                .id(1L)
                .name("user")
                .build();
        RequestCreateDto createDto = RequestCreateDto.builder()
                .id(1L)
                .description("description")
                .created(LocalDateTime.now())
                .build();
        Request request = mapper.toRequest(createDto);

        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(requestor));
        when(requestRepository.save(any(Request.class))).thenReturn(request);
        RequestCreateDto save = service.createRequest(createDto, 1L);

        assertNotNull(save);
        verify(userRepository, times(1)).findById(anyLong());
        verify(requestRepository, times(1)).save(any());
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    void getRequests() {
        User requestor = User.builder()
                .id(1L)
                .name("user")
                .build();
        Request request = Request.builder()
                .id(1L)
                .user(requestor)
                .created(LocalDateTime.now())
                .build();
        Request request2 = Request.builder()
                .id(2L)
                .user(requestor)
                .created(LocalDateTime.now())
                .build();
        Item item = Item.builder()
                .id(1L)
                .request(request)
                .build();
        Item item2 = Item.builder()
                .id(2L)
                .request(request2)
                .build();
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(requestor));
        when(requestRepository.findAllByUser_Id(anyLong())).thenReturn(List.of(request, request2));
        when(itemRepository.findItemsByRequestIdIn(anyList())).thenReturn(List.of(item, item2));

        List<RequestItemsDto> requests = service.getRequests(1L);

        assertNotNull(requests);
        assertEquals(2, requests.size());
        assertEquals(1L, requests.get(0).getId());
        assertEquals(2L, requests.get(1).getId());

        verify(userRepository, times(1)).findById(anyLong());
        verify(requestRepository, times(1)).findAllByUser_Id(anyLong());
        verify(itemRepository, times(1)).findItemsByRequestIdIn(anyList());
    }

    @Test
    void getRequestsThrowsNotFoundException() {
        assertThrows(NotFoundException.class, () -> service.getRequests(1L));
    }

    @Test
    void requestsGetAll() {
        Long from = 0L;
        Long size = 10L;
        PageRequest pageRequest = PageRequest.of(from.intValue(), size.intValue());
        User requestor = User.builder()
                .id(1L)
                .name("user")
                .build();
        User anotherUser = User.builder()
                .id(2L)
                .name("user2")
                .build();
        Request request = Request.builder()
                .id(1L)
                .user(anotherUser)
                .created(LocalDateTime.now())
                .build();
        Request request2 = Request.builder()
                .id(2L)
                .user(anotherUser)
                .created(LocalDateTime.now())
                .build();
        Item item = Item.builder()
                .id(1L)
                .request(request)
                .build();
        Item item2 = Item.builder()
                .id(2L)
                .request(request2)
                .build();
        List<Request> list = List.of(request, request2);
        Page<Request> requests = new PageImpl<>(list);
        when(requestRepository.findAllByUser_IdNotOrderByCreatedDesc(
                anyLong(),
                eq(pageRequest)))
                .thenReturn(requests);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(requestor));
        when(itemRepository.findItemsByRequestIdIn(anyList())).thenReturn(List.of(item, item2));

        List<RequestItemsDto> all = service.requestsGetAll(2L, from, size);

        assertNotNull(all);
        assertEquals(1L, all.get(0).getId());
        assertEquals(2L, all.get(1).getId());
        assertEquals(1L, all.get(0).getItems().get(0).getId());
        assertEquals(2L, all.get(1).getItems().get(0).getId());
        assertEquals(1L, all.get(0).getItems().get(0).getRequestId());
        assertEquals(2L, all.get(1).getItems().get(0).getRequestId());
    }

    @Test
    void getAllRequestsThrowsNotFoundException() {
        assertThrows(NotFoundException.class, () -> service.requestsGetAll(1L, 0L, 10L));
    }

    @Test
    void getRequestByIdNotFoundUserException() {
        Throwable exception = assertThrows(NotFoundException.class, () -> service.getRequestById(1L, 1L));
        assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    void getRequestByIdNotFoundRequestException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        Throwable exception = assertThrows(NotFoundException.class, () -> service.getRequestById(1L, 1L));
        assertEquals("Запрос не найден", exception.getMessage());
    }

    @Test
    void getRequestByIdCorrect() {
        User user = User.builder()
                .id(1L)
                .build();
        Request request = Request.builder()
                .id(1L)
                .user(user)
                .build();
        Item item = Item.builder()
                .id(1L)
                .request(request)
                .build();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findItemByRequestId(anyLong())).thenReturn(item);
        when(requestRepository.findById(anyLong())).thenReturn(Optional.ofNullable(request));

        RequestItemsDto dto = service.getRequestById(1L, 1L);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals(1L, dto.getItems().get(0).getId());
        assertEquals(1L, dto.getItems().get(0).getRequestId());

        verify(userRepository, times(1)).findById(anyLong());
        verify(requestRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findItemByRequestId(anyLong());
    }
}