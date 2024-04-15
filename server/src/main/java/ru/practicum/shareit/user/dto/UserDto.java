package ru.practicum.shareit.user.dto;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class UserDto {
    private Long id;
    private String name;
    private String email;
}
