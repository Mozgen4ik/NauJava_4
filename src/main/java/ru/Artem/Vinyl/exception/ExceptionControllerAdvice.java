package ru.Artem.Vinyl.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Глобальный обработчик исключений для всех контроллеров.
 *
 * Аннотация @ControllerAdvice применяет этот класс ко всем контроллерам в приложении.
 * Все необработанные исключения будут перехвачены методами этого класса.
 */
@ControllerAdvice
public class ExceptionControllerAdvice {

    /**
     * Обработка общих исключений (Exception).
     * Возвращает HTTP статус 500 (Internal Server Error).
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiException handleGeneralException(Exception e) {
        return ApiException.create(e);
    }

    /**
     * Обработка исключения "ресурс не найден".
     * Возвращает HTTP статус 404 (Not Found).
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiException handleResourceNotFoundException(ResourceNotFoundException e) {
        return ApiException.create(e);
    }

    /**
     * Обработка исключений с некорректными параметрами.
     * Возвращает HTTP статус 400 (Bad Request).
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiException handleIllegalArgumentException(IllegalArgumentException e) {
        return ApiException.create(e);
    }
}