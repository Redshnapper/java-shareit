package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestCreateDto;
import ru.practicum.shareit.request.dto.RequestItemsDto;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.util.Constants;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class RequestController {
    private final RequestService requestService;

    @PostMapping
    public RequestCreateDto createRequest(@RequestHeader(Constants.USER_HEADER_ID) Long userId,
                                          @RequestBody RequestCreateDto createDto) {
        log.info("Добавление запроса createDto = {}, пользователем userId = {}", createDto, userId);
        return requestService.createRequest(createDto, userId);
    }

    @GetMapping
    public List<RequestItemsDto> getRequests(@RequestHeader(Constants.USER_HEADER_ID) Long userId) {
        log.info("Запрос на получение списка своих запросов, пользователем userId = {}", userId);
        return requestService.getRequests(userId);
    }

    @GetMapping("all")
    public List<RequestItemsDto> requestsGetAll(@RequestHeader(Constants.USER_HEADER_ID) Long userId,
                                                @RequestParam(name = "from", defaultValue = "0") Long from,
                                                @RequestParam(name = "size", defaultValue = "10") Long size) {
        log.info("Запрос на получение списка всех запросов постранично, from = {}, size = {}, пользователем userId = {}",
                from, size, userId);
        return requestService.requestsGetAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public RequestItemsDto getRequestById(@RequestHeader(Constants.USER_HEADER_ID) Long userId, @PathVariable Long requestId) {
        log.info("Запрос на получение запроса, пользователем userId = {}, requestId = {}", userId, requestId);
        return requestService.getRequestById(userId, requestId);
    }
}
