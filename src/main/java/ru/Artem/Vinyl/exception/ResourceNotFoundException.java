package ru.Artem.Vinyl.exception;

/**
 * Исключение для случаев, когда запрашиваемый ресурс не найден.
 * Например, когда пытаемся получить пластинку по несуществующему ID.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}