package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemRequestDto;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final RequestMapper mapper;
    private final ModelMapper modelMapper;

    @Override
    public RequestCreateDto createRequest(RequestCreateDto createDto, Long userId) {
        Request created = addRequest(createDto, userId);
        return mapper.toDto(created);
    }

    @Override
    public List<RequestItemsDto> getRequests(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        List<Request> requests = requestRepository.findAllByUser_Id(userId);

        Map<Long, List<ItemRequestDto>> itemsByRequestId = itemRepository.findItemsByRequestIdIn(
                        requests.stream()
                                .map(Request::getId)
                                .collect(Collectors.toList())
                ).stream()
                .map(item -> modelMapper.map(item, ItemRequestDto.class))
                .collect(Collectors.groupingBy(ItemRequestDto::getRequestId));

        return requests.stream()
                .map(request -> new RequestItemsDto(
                        request.getId(),
                        request.getDescription(),
                        request.getCreated(),
                        itemsByRequestId.getOrDefault(request.getId(), Collections.emptyList())
                ))
                .collect(Collectors.toList());
    }


    @Override
    public List<RequestItemsDto> requestsGetAll(Long userId, Long from, Long size) {
        int fromInt = from.intValue();
        int sizeInt = size.intValue();

        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Page<Request> requests = requestRepository.findAllByUser_IdNotOrderByCreatedDesc(userId, PageRequest.of(fromInt, sizeInt));

        return requests.map(request -> {
            List<Long> requestIds = Collections.singletonList(request.getId());
            Map<Long, List<ItemRequestDto>> itemsByRequestId = itemRepository.findItemsByRequestIdIn(requestIds).stream()
                    .map(item -> modelMapper.map(item, ItemRequestDto.class))
                    .collect(Collectors.groupingBy(ItemRequestDto::getRequestId));

            return new RequestItemsDto(
                    request.getId(),
                    request.getDescription(),
                    request.getCreated(),
                    itemsByRequestId.getOrDefault(request.getId(), Collections.emptyList())
            );
        }).getContent();

    }

    @Override
    public RequestItemsDto getRequestById(Long userId, Long requestId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Request request = requestRepository.findById(requestId).orElseThrow(() -> new NotFoundException("Запрос не найден"));
        Item item = itemRepository.findItemByRequestId(requestId);

        return new RequestItemsDto(
                request.getId(),
                request.getDescription(),
                request.getCreated(),
                List.of(modelMapper.map(item, ItemRequestDto.class))
        );
    }


    private Request addRequest(RequestCreateDto createDto, Long userId) {
        Request request = mapper.toRequest(createDto);
        User requestor = userRepository.findById(userId).orElseThrow();
        request.setUser(requestor);
        request.setCreated(LocalDateTime.now());
        return requestRepository.save(request);
    }
}
