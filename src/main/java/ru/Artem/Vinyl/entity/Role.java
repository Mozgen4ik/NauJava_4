package ru.Artem.Vinyl.entity;

/**
 * Роли пользователей в системе.
 *
 * ADMIN - администратор (полный доступ, включая Swagger UI)
 * USER - обычный пользователь с ограниченным доступом
 */
public enum Role {
    ADMIN,
    USER
}