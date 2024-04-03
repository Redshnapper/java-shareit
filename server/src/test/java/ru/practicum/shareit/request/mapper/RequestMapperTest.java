package ru.practicum.shareit.request.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.request.dto.RequestCreateDto;
import ru.practicum.shareit.request.model.Request;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class RequestMapperTest {

    @Autowired
    private RequestMapper requestMapper;

    @Test
    public void testToDto() {
        LocalDateTime now = LocalDateTime.now();
        Request request = new Request(1L, "Test Description", null, now);
        RequestCreateDto requestCreateDto = requestMapper.toDto(request);

        assertNotNull(requestCreateDto);
        assertEquals(request.getId(), requestCreateDto.getId());
        assertEquals(request.getDescription(), requestCreateDto.getDescription());
        assertEquals(request.getCreated(), requestCreateDto.getCreated());
    }

    @Test
    public void testToRequest() {
        LocalDateTime now = LocalDateTime.now();
        RequestCreateDto requestCreateDto = new RequestCreateDto(1L, "Test Description", now);
        Request request = requestMapper.toRequest(requestCreateDto);

        assertNotNull(request);
        assertEquals(requestCreateDto.getId(), request.getId());
        assertEquals(requestCreateDto.getDescription(), request.getDescription());
        assertEquals(requestCreateDto.getCreated(), request.getCreated());
    }
}