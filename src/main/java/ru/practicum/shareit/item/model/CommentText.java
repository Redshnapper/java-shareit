package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
public class CommentText {
    @NotBlank
    private String text;
}
