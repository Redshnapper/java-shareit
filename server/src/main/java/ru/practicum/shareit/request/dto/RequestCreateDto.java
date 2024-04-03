package ru.practicum.shareit.request.dto;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@Builder
public class RequestCreateDto {
    private Long id;
    private String description;
    private LocalDateTime created;
}
