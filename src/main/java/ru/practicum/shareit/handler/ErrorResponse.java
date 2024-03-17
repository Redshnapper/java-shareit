package ru.practicum.shareit.handler;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@Getter
public class ErrorResponse {
    private final Map<String, String> errors = new HashMap<>();


}

