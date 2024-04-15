package ru.practicum.shareit.item.dto;

import lombok.*;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class ItemRequestDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
}
