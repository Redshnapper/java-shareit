package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotEmpty;

@AllArgsConstructor
@RequiredArgsConstructor
@Data
public class CommentText {
    @NotEmpty
    private String text;
}
