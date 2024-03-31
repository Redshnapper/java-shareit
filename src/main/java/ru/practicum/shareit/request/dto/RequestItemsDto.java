package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.item.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@Builder
public class RequestItemsDto {
    private Long id;
    private String description;
    private LocalDateTime created;
    private List<ItemRequestDto> items;
}
