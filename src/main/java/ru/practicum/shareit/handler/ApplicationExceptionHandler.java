package ru.practicum.shareit.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ApplicationExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFoundException(NotFoundException e) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.getErrors().put("errorMessage", e.getLocalizedMessage());
        log.error(e.getLocalizedMessage());
        return errorResponse;
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public SimpleErrorResponse handleBadRequestException(BadRequestException e) {
        SimpleErrorResponse errorResponse = new SimpleErrorResponse(e.getLocalizedMessage());
        log.error(e.getLocalizedMessage());
        return errorResponse;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidException(MethodArgumentNotValidException e) {
        ErrorResponse errorResponse = new ErrorResponse();
        Map<String, String> exceptions = errorResponse.getErrors();
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            exceptions.put(error.getField(), error.getDefaultMessage());
            log.error("Поле {} не прошло валидацию. Причина: {}.", error.getField(), error.getDefaultMessage());
        }

        return errorResponse;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleAllException(Exception e) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.getErrors().put("errorMessage", e.getLocalizedMessage());
        log.error(e.getLocalizedMessage());
        return errorResponse;
    }
}
