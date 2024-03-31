package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.request.dto.RequestCreateDto;
import ru.practicum.shareit.request.model.Request;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public class RequestMapper {
    public RequestCreateDto toDto(Request request) {
        RequestCreateDto requestCreateDto = new RequestCreateDto();
        requestCreateDto.setId(request.getId());
        requestCreateDto.setDescription(request.getDescription());
        requestCreateDto.setCreated(request.getCreated());
        return requestCreateDto;
    }

    public Request toRequest(RequestCreateDto createDto) {
        Request request = new Request();
        request.setId(createDto.getId());
        request.setDescription(createDto.getDescription());
        request.setCreated(createDto.getCreated());
        return request;
    }
}
