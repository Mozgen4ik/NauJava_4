package ru.Artem.Vinyl.exception;

/**
 * Класс-обёртка для представления ошибок в REST API.
 * Используется для унифицированного формата ответов об ошибках.
 */
public class ApiException {

    private String message;

    // Приватный конструктор - создание только через статические методы
    private ApiException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Создаёт ApiException из любого Throwable.
     */
    public static ApiException create(Throwable e) {
        return new ApiException(e.getMessage());
    }

    /**
     * Создаёт ApiException из строки сообщения.
     */
    public static ApiException create(String message) {
        return new ApiException(message);
    }
}