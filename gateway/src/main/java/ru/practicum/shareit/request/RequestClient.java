package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import ru.practicum.shareit.request.dto.RequestCreateDto;

import java.util.Map;

@Service
public class RequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    public RequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build());
    }

    public ResponseEntity<Object> createRequest(Long userId, RequestCreateDto createDto) {
        return post("", userId, createDto);
    }

    public ResponseEntity<Object> getRequests(Long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> requestsGetAll(Long userId, Long from, Long size) {
        return get("/all?from={from}&size={size}", userId, Map.of("from", from, "size", size));
    }

    public ResponseEntity<Object> getRequestById(Long userId, Long requestId) {
        return get("/{requestId}", userId, Map.of("requestId", requestId));
    }
}
