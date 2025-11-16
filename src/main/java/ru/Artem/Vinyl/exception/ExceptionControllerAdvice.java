package ru.Artem.Vinyl.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Глобальный обработчик исключений для всех контроллеров.
 *
 */
@ControllerAdvice
public class ExceptionControllerAdvice {

    /**
     * Обработка общих исключений.
     * Возвращает HTTP статус 500.
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiException handleGeneralException(Exception e) {
        return ApiException.create(e);
    }

    /**
     * Обработка исключения "ресурс не найден".
     * Возвращает HTTP статус 404.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiException handleResourceNotFoundException(ResourceNotFoundException e) {
        return ApiException.create(e);
    }

    /**
     * Обработка исключений с некорректными параметрами.
     * Возвращает HTTP статус 400.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiException handleIllegalArgumentException(IllegalArgumentException e) {
        return ApiException.create(e);
    }
}