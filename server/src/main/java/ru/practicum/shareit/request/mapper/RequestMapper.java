package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.request.dto.RequestCreateDto;
import ru.practicum.shareit.request.model.Request;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface RequestMapper {
    RequestCreateDto toDto(Request request);

    Request toRequest(RequestCreateDto createDto);
}
