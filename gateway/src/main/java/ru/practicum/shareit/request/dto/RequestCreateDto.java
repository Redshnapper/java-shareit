package ru.practicum.shareit.request.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@Builder
public class RequestCreateDto {
    private Long id;
    @NotNull
    private String description;
    private LocalDateTime created;
}
