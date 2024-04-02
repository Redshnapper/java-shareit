package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.RequestCreateDto;
import ru.practicum.shareit.request.dto.RequestItemsDto;

import java.util.List;

public interface RequestService {
    RequestCreateDto createRequest(RequestCreateDto createDto, Long userId);

    List<RequestItemsDto> getRequests(Long userId);

    List<RequestItemsDto> requestsGetAll(Long userId, Long from, Long size);

    RequestItemsDto getRequestById(Long userId, Long requestId);
}
