package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestCreateDto;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class RequestController {
    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @RequestBody @Valid RequestCreateDto createDto) {
        log.info("Creating request: createDto = {}, user = {}", createDto, userId);
        return requestClient.createRequest(userId, createDto);
    }

    @GetMapping
    public ResponseEntity<Object> getRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Getting list of own requests, user = {}", userId);
        return requestClient.getRequests(userId);
    }

    @GetMapping("all")
    public ResponseEntity<Object> requestsGetAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @RequestParam(name = "from", required = false, defaultValue = "0") Long from,
                                                 @RequestParam(name = "size", required = false, defaultValue = "10") Long size) {
        log.info("Getting list of all requests paginated, from = {}, size = {}, user = {}", from, size, userId);
        return requestClient.requestsGetAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @PathVariable Long requestId) {
        log.info("Getting request by id, user = {}, requestId = {}", userId, requestId);
        return requestClient.getRequestById(userId, requestId);
    }
}
